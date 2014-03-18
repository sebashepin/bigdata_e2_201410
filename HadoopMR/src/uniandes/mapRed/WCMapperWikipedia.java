package uniandes.mapRed;

import java.io.IOException;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class WCMapperWikipedia extends Mapper<LongWritable, Text, Text, Text> {

	private class ReadUntilResult{
		private StringBuilder text;
		private int lastIndex;
		private ReadUntilResult(int lastIndex) {
			this.text = new StringBuilder();
			this.lastIndex = lastIndex;
		}		
	}
	
	private final static String PAGE_START = "<page>";
	private final static String PAGE_END = "</page>";
	private final static String TITLE_START = "<title>";
	private final static String TITLE_COMPLETE = TITLE_START+"(.|\\s){0,}?</title>";
	private final static Pattern RELATED_ARTICLE_PATTERN = Pattern.compile("\\[\\[(.|\\s){0,}?\\]\\]");
	private final static String INFOBOX_START = "\\{\\{infobox";
	private final static Pattern INFOBOX_START_PATTERN = Pattern.compile(INFOBOX_START,Pattern.CASE_INSENSITIVE);	
	private final static String PERSONDATA_START = "\\{\\{persondata";	
	private final static Pattern PERSONDATA_START_PATTERN = Pattern.compile(PERSONDATA_START,Pattern.CASE_INSENSITIVE);
	
	public ReadUntilResult readUntil(int startPos, Text text, String exitRegex){
		ReadUntilResult rur = new ReadUntilResult(startPos);
		int curChar;
		while(!rur.text.toString().matches(exitRegex) && rur.lastIndex < text.getLength() && (curChar= text.charAt(rur.lastIndex++)) != -1)
			rur.text.append((char)curChar);
		return rur;
	}
	
	public ReadUntilResult readUntilCloseGroup(int startPos, Text text){
		ReadUntilResult rur = new ReadUntilResult(startPos+2);
		rur.text.append("{{");
		int curChar;
		int curLevel = 1;
		int lastChar = -1;
		while(curLevel != 0 && rur.lastIndex < text.getLength() && (curChar= text.charAt(rur.lastIndex++)) != -1){
			char readChar = (char) curChar;
			if(((char)lastChar) == '{' && readChar == '{'){
				lastChar = -1;
				curLevel++;
			}
			else if(((char)lastChar) == '}' && readChar == '}'){
				lastChar = -1;
				curLevel--;
			}
			else
				lastChar = curChar;
				
			rur.text.append((char)curChar);
		}
		return rur;
	}
	
	public String extractTitleFromXML(String text){
		return text.replaceAll("<.{1,}?>", "").trim().replaceAll("\\s", "_");
	}
	
	public TreeSet<String> extractRelatedArticles(String text){
		TreeSet<String> relatedArts = new TreeSet<String>();
		Matcher matcher = RELATED_ARTICLE_PATTERN.matcher(text);
		while(matcher.find()){
			String relatedArt = matcher.group().replaceAll("\\[\\[", "").replaceAll("\\]\\]", "");
			int indexOfPipe = relatedArt.lastIndexOf('|');
			if(indexOfPipe >= 0)
				relatedArt = relatedArt.substring(indexOfPipe+1);
			relatedArts.add(relatedArt.trim().replaceAll("\\s", "_"));
		}
		return relatedArts;
	}
	
	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		if(value.getLength() <= 0)
			return;
			int pagePos = value.find(PAGE_START,0);
		if(pagePos >= 0){
			int pageEndPos = value.find(PAGE_END,pagePos);
			if(pageEndPos > 0){
				boolean persona = false;
				String pageId = null;
				TreeSet<String> related = new TreeSet<String>();
				// Extracts the page ID
				int startPos =  value.find(TITLE_START,pagePos);
				int currentPos = startPos;
				if(startPos < pageEndPos && startPos > 0){
					ReadUntilResult rur = readUntil(startPos, value, TITLE_COMPLETE);
					pageId = extractTitleFromXML(rur.text.toString());
					currentPos = rur.lastIndex;
				}
				else
					return;

				startPos = value.find("{{",currentPos);
				while(startPos < pageEndPos && startPos > 0){
					ReadUntilResult rur = readUntilCloseGroup(startPos, value);
					String content = rur.text.toString();
					if(INFOBOX_START_PATTERN.matcher(content).find()){
						related.addAll(extractRelatedArticles(content));
					}else if(PERSONDATA_START_PATTERN.matcher(content).find()){
						persona = true;
					}					
					
					currentPos = rur.lastIndex;
					if(currentPos > 0 && currentPos < value.getLength()){
						startPos =  value.find("{{",currentPos);
					}
					else
						startPos = -1;
				}
				if(persona){
					for(String relatedArtI:related)
						context.write(new Text(pageId), new Text(relatedArtI));
				}
				
			}
		}
	}

}

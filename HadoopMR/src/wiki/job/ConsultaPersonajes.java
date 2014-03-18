package wiki.job;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import uniandes.inputFormat.NewsInputFormat;
import uniandes.mapRed.WCMapperWikipedia;
import uniandes.mapRed.WCReducerWikipedia;

public class ConsultaPersonajes {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out
					.println("Se necesita las carpetas de entrada y salida y el modo de ejecución");
			System.exit(-1);
		}

		String entrada = args[0]; // carpeta de entrada
		String salida = args[1];// La carpeta de salida no puede existir

		try {
			ejecutarJob(entrada, salida);
		} catch (Exception e) { // Puede ser IOException, ClassNotFoundException
								// o InterruptedException
			e.printStackTrace();
		}

	}

	/**
	 */
	public static void ejecutarJob(String entrada, String salida)
			throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();
		JobConf conf2 = new JobConf();
		conf2.setJarByClass(ConsultaPersonajes.class);

		Job wcJob = new Job(conf, "ConsultaPersonajes Job");
		wcJob.setJarByClass(ConsultaPersonajes.class);

		
		// /////////////////////////
		// Input Format
		// /////////////////////////
		// Advertencia: Hay dos clases con el mismo nombre,
		// pero no son equivalentes.
		// Se usa, en este caso,
		// org.apache.hadoop.mapreduce.lib.input.TextInputFormat
		XmlInputFormat.setInputPaths(wcJob, new Path(entrada));
		
		wcJob.setInputFormatClass(XmlInputFormat.class);

		NewsInputFormat.setInputPaths(wcJob, new Path(entrada));
		wcJob.setMapperClass(WCMapperWikipedia.class);
		wcJob.setMapOutputKeyClass(Text.class);
		wcJob.setMapOutputValueClass(Text.class);

		// /////////////////////////
		// Reducer
		// /////////////////////////
		wcJob.setReducerClass(WCReducerWikipedia.class);
		wcJob.setOutputKeyClass(Text.class);
		wcJob.setOutputValueClass(Text.class);

		// //////////////////
		// /Output Format
		// ////////////////////
		TextOutputFormat.setOutputPath(wcJob, new Path(salida));
		wcJob.setOutputFormatClass(TextOutputFormat.class);
		System.out.println(wcJob.toString());
		wcJob.waitForCompletion(true);
	}
}

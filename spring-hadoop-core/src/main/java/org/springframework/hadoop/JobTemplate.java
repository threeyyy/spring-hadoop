package org.springframework.hadoop;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.springframework.hadoop.context.HadoopApplicationContextUtils;
import org.springframework.util.StringUtils;

public class JobTemplate {

	/**
	 * The default location for a job configuration.
	 */
	public static final String DEFAULT_CONFIG_LOCATION = "/META-INF/spring/hadoop/job-context.xml";

	public static final String SPRING_INPUT_PATHS = "spring.input.paths";

	public static final String SPRING_OUTPUT_PATH = "spring.output.path";

	private boolean verbose = false;

	private String jarFile;

	private String hostname;

	private int port = 9001;

	private Map<String,String> extraConfiguration = new HashMap<String, String>();

	/**
	 * @param verbose the verbose flag to set
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * @param configuration extra configuration to apply
	 */
	public void setExtraConfiguration(Map<String,String> configuration) {
		this.extraConfiguration = configuration;
	}

	public void setJarFile(String jarFile) {
		this.jarFile = jarFile;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean run() throws Exception {
		return run(DEFAULT_CONFIG_LOCATION);
	}

	// TODO: add callback to enhance context?
	public boolean run(String configLocation) throws Exception {

		Job bean = HadoopApplicationContextUtils.getJob(configLocation);

		try {

			// Construct the new complete configuration *before* the Job is initialized...
			Configuration configuration = bean.getConfiguration();
			mergeExtraConfiguration(configuration);
			// Leave the original Job intact (so it can be a singleton).
			Job job = new Job(configuration, bean.getJobName());

			if (configuration.get(SPRING_INPUT_PATHS) != null) {
				for (String path : StringUtils.commaDelimitedListToStringArray(configuration.get(SPRING_INPUT_PATHS))) {
					FileInputFormat.addInputPath(job, new Path(path));
				}
			}
			if (configuration.get(SPRING_OUTPUT_PATH) != null) {
				String path = configuration.get(SPRING_OUTPUT_PATH);
				FileOutputFormat.setOutputPath(job, new Path(path));
			}

			return job.waitForCompletion(verbose);

		}
		finally {
			HadoopApplicationContextUtils.releaseJob(bean);
		}
	}

	private void mergeExtraConfiguration(Configuration configuration) {
		for (Entry<String, String> entry : getExtraConfiguration().entrySet()) {
			configuration.set(entry.getKey(), entry.getValue());
		}
	}

	public Map<String,String> getExtraConfiguration() {
		Map<String, String> map = new HashMap<String, String>(extraConfiguration);
		if (jarFile != null) {
			map.put("mapred.jar", jarFile);
		}
		if (hostname != null) {
			map.put("mapred.job.tracker", String.format("%s:%d", hostname, port));
			// TODO: make fs port configurable
			map.put("fs.default.name", String.format("hdfs://%s:%d/", hostname, port - 1));
		}
		return map;
	}

}

log4j {
	rootLogger="INFO, stdout"
	appender.stdout = "org.apache.log4j.ConsoleAppender"
	appender.'stdout.layout' = "org.apache.log4j.PatternLayout"
	appender.'stdout.layout.ConversionPattern'="prod %-4r [%t] %-5p %c %x - %m%n"
		
	logger.org.apache.http=INFO
	logger.httpclient.wire=INFO
	logger.org.apache.commons.httpclient=INFO
}
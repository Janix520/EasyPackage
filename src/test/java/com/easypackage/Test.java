package com.easypackage;

import java.io.IOException;

public class Test {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

//		String path =  new File(System.getProperty("java.home"), "jmods").getAbsolutePath() + System.getProperty("path.separator") ;
//		String path =  System.getenv("JAVA_HOME") + File.separator + "bin" + File.separator;
//		String path =  System.getProperty("java.home");
//		System.out.println(path);

//		List<String> list = new ArrayList<>();
////		jdeps.exe --multi-release 17 --print-module-deps --ignore-missing-deps --module-path .\* modules\*
//		list.add("jdeps");
//		list.add("--multi-release");
//		list.add("9");
//		list.add("--print-module-deps");
//		list.add("--ignore-missing-deps");
//		list.add("--module-path");
//		list.add("./*");
//		list.add("libs/*");
//
//		ProcessBuilder processBuilder = new ProcessBuilder(list);
//		processBuilder.directory(new File("D:\\workspace-police\\SecondSearch\\target"));
//
//		Process start = processBuilder.start();
//		
//		InputStream inputStream = start.getInputStream();
//		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//		
//		String ss = "";
//		while (true) {
//			String line = bufferedReader.readLine();
//			if (null == line) {
//				break;
//			}
//			ss = line;
//		}
//		
//		System.out.println(ss);
//
//		String jdkVersion = System.getProperty("java.version");
//		System.out.println("JDK Version: " + jdkVersion);
//
//		System.out.println(Runtime.version());

//		String optEncode=System.getProperties().getProperty("sun.jnu.encoding");
//		String fileEncode=System.getProperties().getProperty("file.encoding");
//		String jvmEncode= Charset.defaultCharset().displayName();

		// 打印默认字符编码
//        System.out.println("系统默认编码: " + optEncode);

		String property = System.getProperty("java.home");

		System.out.println(property);

	}

}

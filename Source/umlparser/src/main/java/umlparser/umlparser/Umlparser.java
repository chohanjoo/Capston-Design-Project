package umlparser.umlparser;

import java.lang.reflect.Array;
import java.util.ArrayList;

//import com.google.gson.*;

public class Umlparser {

	public static void main(String[] args) throws Exception {
		if (args[0].equals("class")) {
			ParseEngine pe = new ParseEngine(args[1], args[2]);
			String str = pe.start();
			System.out.println("기존 문자열: " + str);
			String json = makeJsonFile(str);
			System.out.println(json);

		} else if (args[0].equals(("seq"))) {
			ParseSeqEngine pse = new ParseSeqEngine(args[1], args[2], args[3], args[4]);
			pse.start();
		} else {
			System.out.println("Invalid keyword " + args[0]);
		}
	}

	public static String makeJsonFile(String str) {
		ArrayList<String> classArray = new ArrayList<>();
		String[] eachClassArray = str.split(",");
		ArrayList<String> extendsArray = new ArrayList<String>();
		ArrayList<String> implementsArray = new ArrayList<String>();
		ArrayList<String> interfaceArray = new ArrayList<>();
		String json = "";
		int classCount = 0;
		int i, j, k;

		// "totalNumOfClasses"의 값을 classCount에 저장한다. (폴더안의 클래스의 개수를 센다)
		for (String s : eachClassArray) {
			// comma(,) 2번 나오는 경우가 있기 때문에 isEmpty를 써서 내용이 있는 것만 classArray에 넣는다.
			if (!s.isEmpty()) {
				// 상속 표시 "-^"를 포함하지 않는 경우 (이 프로젝트에서 상속은 "-^"로 표시한다. 예) B extends A --> [B] -^
				// [A])
				if (!s.contains("-^") && !s.contains("-.-^")) {
					if (s.contains("<<interface>>"))
						interfaceArray.add(s);
					else {
						classArray.add(s);
						classCount++;
					}
				}
				// 상속을 포함하는 경우
				else if (s.contains("-.-^"))
					implementsArray.add(s);
				else if (s.contains("-^"))
					extendsArray.add(s);

			}
		}

		json += "{\"totalNumOfClasses\":" + classCount + ",";
		json += "\"totalNumOfInterface\":" + interfaceArray.size() + ",";
		json += "\"classes\":[{";

		for (i = 0; i < classArray.size(); i++) {
			String elementArray[] = classArray.get(i).split("\\|");
			String className = elementArray[0].trim().substring(1, elementArray[0].length());
			int totalNumOfVariables = 0;
			int totalNumOfMethods = 0;

			// elementArray.length > 1인 경우 --> 변수가 존재하는 경우
			if (elementArray.length > 1)
				totalNumOfVariables = elementArray[1].trim().split(";").length;

			// commaCount != 0인 경우 --> 메소드가 존재하는 경우
			if (elementArray.length > 2)
				totalNumOfMethods = elementArray[2].trim().split(";").length;

			json += "\"totalNumOfVariables\":" + totalNumOfVariables + ",";
			json += "\"totalNumOfMethods\":" + totalNumOfMethods + ",";
			json += "\"className\":" + "\"" + className + "\"" + ",";
			json += "\"variables\":" + "[{";

			// 변수("variables") 추가하는 과정 (elementArray.length > 1인 경우 --> 변수가 존재하는 경우)
			if (elementArray.length > 1) {
				for (String elem : elementArray[1].split(";")) {
					// elem 문자열 끝에 위치한 ']' 제거(변수가 1개일 경우 뒤에 ']'문자 따라옴)
					if (!elem.equals("")) {
						elem = elem.replaceAll("]$", "");
						json += "\"" + elem.trim().split(":")[0] + "\":" + "\"" + elem.trim().split(":")[1] + "\",";
					}
				}
			}

			// json 문자열 끝에 위치한 ,(comma) 제거
			json = json.replaceAll(",$", "");
			// 메소드를 작성하기 위해 ",}]" 문자 추가("variables" 닫는 과정)
			json += "}],";
			json += "\"methods\":" + "[{";

			// 메소드("methods") 추가하는 과정
			if (elementArray.length > 2) {
				// 메소드가 1개일 경우
				if (elementArray[2].split(";").length == 1) {
					String tmp = elementArray[2].trim();
					tmp = tmp.replaceAll("]$", "");
					int start = tmp.indexOf('(');
					int last = tmp.lastIndexOf(')');
					String parameter = tmp.substring(start, last + 1).trim();
					json += "\"" + tmp.substring(0, start) + "\":\"" + tmp.substring(last + 1).split(":")[1] + parameter
							+ "\"";
				}
				// 메소드가 2개 이상일 경우
				else {
					for (j = 0; j < elementArray[2].split(";").length; j++) {
						String[] tmp = elementArray[2].trim().split(";");
						// 각 메소드 잘라서 json에 붙이는 작업
						for (k = 0; k < tmp.length; k++) {
							int start = tmp[k].indexOf('(');
							int last = tmp[k].lastIndexOf(')');
							String parameter = tmp[k].substring(start, last + 1).trim();
							tmp[k] = tmp[k].replaceAll("]$", "");
							json += "\"" + tmp[k].substring(0, start) + "\":\""
									+ tmp[k].substring(last + 1).split(":")[1] + parameter;
							json += "\",";

						}
					}
				}
			}

			// json 문자열 끝에 위치한 ,(comma) 제거
			json = json.replaceAll(",$", "");
			// 다음 클래스를 작성하기 위해 "}]}," 문자 추가("methods" 닫는 과정)
			json += "}]},";

			// json 문자열안의 공백 제거
			json = json.replaceAll(" ", "");
			// json 문자열 끝에 위치한 ,(comma) 제거
			json = json.replaceAll(",$", "");

			// 다음 객체를 위해서 ",{" 문자 추가
			json += ",{";
		}

		// 마지막 ",{" 문자 제거
		json = json.substring(0, json.length() - 2);

		// 마지막에 "]}" 문자 추가
		json += "]}";

		if (!interfaceArray.isEmpty()) {
			i = 0;
			json = json.substring(0, json.length() - 1);
			json += ",";
			json += "\"interface\":[{";
			String elementArray[] = interfaceArray.get(i).split("\\|");
			String className = elementArray[0].split(";")[1];
			int totalNumOfVariables = 0;
			int totalNumOfMethods = 0;

			// elementArray.length > 1인 경우 --> 변수가 존재하는 경우
			if (elementArray.length > 1)
				totalNumOfVariables = elementArray[1].trim().split(";").length;

			// commaCount != 0인 경우 --> 메소드가 존재하는 경우
			if (elementArray.length > 2)
				totalNumOfMethods = elementArray[2].trim().split(";").length;

			json += "\"totalNumOfVariables\":" + totalNumOfVariables + ",";
			json += "\"totalNumOfMethods\":" + totalNumOfMethods + ",";
			json += "\"className\":" + "\"" + className + "\"" + ",";
			json += "\"variables\":" + "[{";

			// 변수("variables") 추가하는 과정 (elementArray.length > 1인 경우 --> 변수가 존재하는 경우)
			if (elementArray.length > 1) {
				for (String elem : elementArray[1].split(";")) {
					// elem 문자열 끝에 위치한 ']' 제거(변수가 1개일 경우 뒤에 ']'문자 따라옴)
					if (!elem.equals("")) {
						elem = elem.replaceAll("]$", "");
						json += "\"" + elem.trim().split(":")[0] + "\":" + "\"" + elem.trim().split(":")[1] + "\",";
					}
				}
			}

			// json 문자열 끝에 위치한 ,(comma) 제거
			json = json.replaceAll(",$", "");
			// 메소드를 작성하기 위해 ",}]" 문자 추가("variables" 닫는 과정)
			json += "}],";
			json += "\"methods\":" + "[{";

			// 메소드("methods") 추가하는 과정
			if (elementArray.length > 2) {
				// 메소드가 1개일 경우
				if (elementArray[2].split(";").length == 1) {
					String tmp = elementArray[2].trim();
					tmp = tmp.replaceAll("]$", "");
					int start = tmp.indexOf('(');
					int last = tmp.lastIndexOf(')');
					String parameter = tmp.substring(start, last + 1).trim();
					json += "\"" + tmp.substring(0, start) + "\":\"" + tmp.substring(last + 1).split(":")[1] + parameter
							+ "\"";
				}
				// 메소드가 2개 이상일 경우
				else {
					for (j = 0; j < elementArray[2].split(";").length; j++) {
						String[] tmp = elementArray[2].trim().split(";");
						// 각 메소드 잘라서 json에 붙이는 작업
						for (k = 0; k < tmp.length; k++) {
							int start = tmp[k].indexOf('(');
							int last = tmp[k].lastIndexOf(')');
							String parameter = tmp[k].substring(start, last + 1).trim();
							tmp[k] = tmp[k].replaceAll("]$", "");
							json += "\"" + tmp[k].substring(0, start) + "\":\""
									+ tmp[k].substring(last + 1).split(":")[1] + parameter;
							json += "\",";

						}
					}
				}
			}

			// json 문자열 끝에 위치한 ,(comma) 제거
			json = json.replaceAll(",$", "");
			// 다음 클래스를 작성하기 위해 "}]}," 문자 추가("methods" 닫는 과정)
			json += "}]},";

			// json 문자열안의 공백 제거
			json = json.replaceAll(" ", "");
			// json 문자열 끝에 위치한 ,(comma) 제거
			json = json.replaceAll(",$", "");

			// 다음 객체를 위해서 ",{" 문자 추가
			json += ",{";

			/*
			 * // 마지막 ",{" 문자 제거 json = json.substring(0, json.length() - 1);
			 * 
			 * // 마지막에 "]}" 문자 추가 json += "}";
			 */
		}
		// 마지막 ",{" 문자 제거
		json = json.substring(0, json.length() - 2);

		// 마지막에 "]}" 문자 추가
		json += "]}";

		if (!extendsArray.isEmpty()) {
			json = json.substring(0, json.length() - 1);
			json += ",";
			json += "\"extends\":[{";
			for (int m = 0; m < extendsArray.size(); ++m) {
				String line = extendsArray.get(m);
				int start, last;
				start = line.indexOf(']');
				last = line.lastIndexOf('[');
				String separate = line.substring(start + 1, last).trim();
				String child = line.substring(0, start + 1);
				String parent = line.substring(last);

				if (separate.equals("-^")) {
					json += "\"" + child.substring(1, child.length() - 1) + "\":" + "\""
							+ parent.substring(1, parent.length() - 1) + "\",";
				}

				json = json.substring(0, json.length() - 1);
				json += "}],";
			}

			// 마지막 ",{" 문자 제거
			json = json.substring(0, json.length() - 1);

			// 마지막에 "]}" 문자 추가
			json += "}";
		}

		if (!implementsArray.isEmpty()) {
			json = json.substring(0, json.length() - 1);
			json += ",";
			json += "\"implements\":[{";
			for (int m = 0; m < implementsArray.size(); ++m) {
				String line = implementsArray.get(m);
				System.out.println(line);
				int start, last;
				start = line.indexOf(']');
				last = line.lastIndexOf('[');
				String separate = line.substring(start + 1, last).trim();
				String child = line.substring(0, start + 1);
				String parent = line.substring(last);
				if (separate.equals("-.-^")) {
					json += "\"" + child.substring(1, child.length() - 1) + "\":" + "\""
							+ parent.substring(15, parent.length() - 1) + "\",";
				}
			}
			// 마지막 ",{" 문자 제거
			json = json.substring(0, json.length() - 1);

			// 마지막에 "]}" 문자 추가
			json += "}]}";
		}

		return json;
	}
}
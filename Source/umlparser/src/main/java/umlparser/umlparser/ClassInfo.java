package umlparser.umlparser;

import java.util.ArrayList;

public class ClassInfo {
	int x, y, width, height;
	private int totalNumOfVariables;
	private int totalNumOfMethods;
	private String className;
	private String access;
	private ArrayList<Variable> variables;
	private ArrayList<Method> methods;

	public ClassInfo() {
		x = 0;
		y = 0;
		width = 0;
		height = 0;
		totalNumOfVariables = 0;
		totalNumOfMethods = 0;
		className = null;
		access = null;
		variables = new ArrayList<>();
		methods = new ArrayList<>();
	}

	public int getVarinum() {
		return totalNumOfVariables;
	}

	public void setVarinum(int totalNumOfVariables) {
		this.totalNumOfVariables = totalNumOfVariables;
	}

	public int getMethodnum() {
		return totalNumOfMethods;
	}

	public void setMethodnum(int totalNumOfMethods) {
		this.totalNumOfMethods = totalNumOfMethods;
	}

	public String getClassname() {
		return className;
	}

	public void setClassname(String className) {
		this.className = className;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public Variable getVarilist(int index) {
		return variables.get(index);
	}

	public void setVariables(ArrayList<Variable> variables) {
		this.variables = variables;
	}

	public Method getMethodlist(int index) {
		return methods.get(index);
	}

	public void addMethodlist(Method method) {
		methods.add(method);
	}

	public void setMethodlist() {
		methods = new ArrayList<Method>();
	}

}

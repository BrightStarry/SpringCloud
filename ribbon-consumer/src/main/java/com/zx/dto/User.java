package com.zx.dto;

/**
 * author:ZhengXing
 * datetime:2018/1/9 0009 10:39
 * 测试传输类
 */
public class User {

	private String name;
	private String pwd;

	public User(String name, String pwd) {
		this.name = name;
		this.pwd = pwd;
	}

	public User() {
	}

	@Override
	public String toString() {
		return "User{" +
				"name='" + name + '\'' +
				", pwd='" + pwd + '\'' +
				'}';
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
}

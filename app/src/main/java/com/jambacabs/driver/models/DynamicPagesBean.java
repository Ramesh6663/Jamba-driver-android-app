package com.jambacabs.driver.models;

import java.util.List;

public class DynamicPagesBean{
	private List<DataItem> data;
	private String type;
	private String message;

	public void setData(List<DataItem> data){
		this.data = data;
	}

	public List<DataItem> getData(){
		return data;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public class DataItem{
		private String to;
		private String id;
		private String body;
		private int order;

		public void setTo(String to){
			this.to = to;
		}

		public String getTo(){
			return to;
		}

		public void setId(String id){
			this.id = id;
		}

		public String getId(){
			return id;
		}

		public void setBody(String body){
			this.body = body;
		}

		public String getBody(){
			return body;
		}

		public void setOrder(int order){
			this.order = order;
		}

		public int getOrder(){
			return order;
		}
	}

}
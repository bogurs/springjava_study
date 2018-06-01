package com.ktds.test;

import java.sql.Timestamp;

public class CommentVO {
	private int comment_num;
	private String id;
	private String comment_content;
	private Timestamp comment_date;
	private int article_num;
	public int getComment_num() {
		return comment_num;
	}
	public void setComment_num(int comment_num) {
		this.comment_num = comment_num;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getComment_content() {
		return comment_content;
	}
	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}
	public Timestamp getComment_date() {
		return comment_date;
	}
	public void setComment_date(Timestamp comment_date) {
		this.comment_date = comment_date;
	}
	public int getArticle_num() {
		return article_num;
	}
	public void setArticle_num(int article_num) {
		this.article_num = article_num;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + comment_num;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CommentVO other = (CommentVO) obj;
		if (comment_num != other.comment_num)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "CommentVO [comment_num=" + comment_num + ", id=" + id
				+ ", comment_content=" + comment_content + ", comment_date="
				+ comment_date + ", article_num=" + article_num + "]";
	}

}

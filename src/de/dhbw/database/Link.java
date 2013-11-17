package de.dhbw.database;

public class Link {
	
	private int id;
	private String name;
	private String url;
	private String image;
	
	public Link() {
		// TODO Auto-generated constructor stub
	}
	
	public Link(String name, String url, String image) {
		
		this.name = name;
		this.url = url;
		this.image = image;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
}

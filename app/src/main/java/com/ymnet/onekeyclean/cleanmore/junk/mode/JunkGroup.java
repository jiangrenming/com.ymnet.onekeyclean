package com.ymnet.onekeyclean.cleanmore.junk.mode;


import java.util.List;

public class JunkGroup {

	/**
	 * 
	 */
	private String id;
	private String name;
	private List<JunkChild> childrenItems;
	private int select;
	private long size;

	public JunkGroup() {
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<JunkChild> getChildrenItems() {
		return childrenItems;
	}

	public void setChildrenItems(List<JunkChild> childrenItems) {
		this.childrenItems = childrenItems;
	}

	public int getSelect() {
		return select;
	}

	public void setSelect(int select) {
		this.select = select;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "JunkGroup{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", childrenItems=" + childrenItems +
				", select=" + select +
				", size=" + size +
				'}';
	}
}

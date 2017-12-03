package courses;

import java.util.*;

interface MyVector<Element> extends Iterator<Element>{
	public abstract Element get(int index) throws Exception;
	public abstract void set(int index, Element E) throws Exception;
	public abstract int length();
	public abstract void push(Element ele);
	public abstract Element pop();
	public abstract void insert(int index, Element ele);
}

public class FakeVectorClass<Element> implements MyVector<Element>{
	private int cursor;
	private int length;
	private int capacity;
	private Element[] arr;
	@SuppressWarnings("unchecked")
	public FakeVectorClass() {
		this.cursor = 0;
		this.length = 0;
		this.capacity = 10;
		this.arr = (Element[])new Object[10];
	}
	//Function from iterator
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		if(cursor > this.length - 1) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Element next() {
		// TODO Auto-generated method stub
		if(this.hasNext()) {
			cursor ++;
			return (Element)this.arr[cursor - 1];
		} else {
			//cursor = 0;
			return null;
		}
	}
	//Function form MyVector
	@Override
	public Element get(int index) throws Exception {
		// TODO Auto-generated method stub
		if(index < 0 || index >= length) {
			throw new Exception("Index Out Of Bounds.");
		} else {
			return (Element)this.arr[index];
		}
	}
	@Override
	public void set(int index, Element E) throws Exception {
		// TODO Auto-generated method stub
		if(index < 0 || index >=this.length) {
			this.arr[index] = E;
		} else {
			throw new Exception("Index Out Of Bounds.");
		}
	}
	@Override
	public int length() {
		// TODO Auto-generated method stub
		return this.length;
	}
	@Override
	public void push(Element ele) {
		// TODO Auto-generated method stub
		if(this.length < this.capacity) {
			this.arr[this.length] = ele;
			this.length ++;
		} else {
			this.doubleCapacity();
			this.arr[this.length] = ele;
			this.length ++;
		}
	}
	@Override
	public Element pop() {
		// TODO Auto-generated method stub
		if(this.length > 0) {
			@SuppressWarnings("unchecked")
			Element tmp = (Element)new Object();
			tmp = this.arr[this.length - 1];
			this.arr[this.length - 1] = null;
			this.length --;
			return tmp;
		} else {
			return null;
		}
	}
	@Override
	public void insert(int index, Element ele) {
		// TODO Auto-generated method stub
		if(index>(length -1)) {
			System.out.println("Error, index out of bounds");
		} else {
			if(length + 1 > capacity) {
				this.doubleCapacity();
			}
			Element prevE = (Element)new Object();
			Element nextE = (Element)new Object();
			prevE = this.arr[index];
			nextE = prevE;
			this.arr[index] = ele;
			for(int i = index + 1; i < this.length + 1; i ++) {
				prevE = this.arr[i];
				this.arr[i] = nextE;
				if(i != this.length ) {
					nextE = prevE;
				}
			}
			this.length ++;
		}
	}
	
	private void doubleCapacity() {
		@SuppressWarnings("unchecked")
		Element[] tmpList = (Element[])new Object[this.capacity*2];
		for(int i = 0; i < this.length; i++) {
			tmpList[i] = this.arr[i];
		}
		this.arr = tmpList;
		this.capacity = this.capacity*2;
	}
	
	public void resetCursor() {
		this.cursor = 0;
	}
	
	public static void main(String[] args) throws Exception {
		FakeVectorClass<Integer> myList = new FakeVectorClass<Integer>();
		for(int i = 1; i < 21; i ++) {
			myList.push(i);
		}
		myList.insert(5, 100);
		while(myList.hasNext()) {
			System.out.println(myList.next());
		}
		myList.resetCursor();
		System.out.println(myList.get(1));
		
	}
}

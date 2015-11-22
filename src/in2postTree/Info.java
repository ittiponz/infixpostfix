package in2postTree;


public class Info {
	private String nodeValue;
	
	public Info(String nodeValue) {
		this.nodeValue = nodeValue;

	}
	
	public String getNodeValue() {
		return nodeValue;
	}


	public void setNodeValue(String nodeValue) {
		this.nodeValue = nodeValue;
	}


	public String toString() {
        return nodeValue;
    }
}

package in2postTree;


import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.ImageIcon;

import java.util.Scanner;
import java.util.Stack;
import java.awt.Dimension;
import java.awt.GridLayout;

public class TreeCalculator extends JPanel implements TreeSelectionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JEditorPane htmlPane;
    private JTree tree;
    private static Scanner scanner = new Scanner(System.in);
	private static String Input="";

    private static boolean DEBUG = false;
    double ans = 0;

    public TreeCalculator() {
        super(new GridLayout(1,0));
        
    	BinaryTree bTree = new BinaryTree();
    	
    	String postFix = Input;
//    	String postFix = "12+";
    	bTree = genTree(postFix);
		

        //Create the nodes.
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(new Info(bTree.value));
        createNodes(top,bTree,postFix);

        //Create a tree that allows one selection at a time.
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Set the icon for leaf nodes.
//        ImageIcon leafIcon = createImageIcon("images/middle.gif");
//        if (leafIcon != null) {
//            DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
//            renderer.setLeafIcon(leafIcon);
//            tree.setCellRenderer(renderer);
//        } else {
//            System.err.println("Leaf icon missing; using default.");
//        }


        tree.addTreeSelectionListener(this);


        JScrollPane treeView = new JScrollPane(tree);


        htmlPane = new JEditorPane();
        htmlPane.setEditable(false);
    
        JScrollPane htmlView = new JScrollPane(htmlPane);


        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(htmlView);

        Dimension minimumSize = new Dimension(100, 50);
        htmlView.setMinimumSize(minimumSize);
        treeView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(200); //XXX: ignored in some releases
                                           //of Swing. bug 4101306
        //workaround for bug 4101306:
        //treeView.setPreferredSize(new Dimension(100, 100)); 

        splitPane.setPreferredSize(new Dimension(500, 300));

        //Add the split pane to this panel.
        add(splitPane);
    }

    /** Required by TreeSelectionListener interface. */
    
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
        
        if (node == null) return;
        
        Object nodeInfo = node.getUserObject();
        
        if(node.isRoot()){
        	Info info = (Info) nodeInfo;
        	System.out.println(info);
        }
        if (node.isLeaf()) {
        	

        	Info info = (Info) nodeInfo;
        	displayResult(info.getNodeValue());
            if (DEBUG) {
                System.out.print(info.getNodeValue() + ":  \n    ");
            }
        } else {

        	String str = getResult(node);
        	displayResult(str);
        	
        }
        if (DEBUG) {
            System.out.println(nodeInfo.toString());
        }
        
        
        
    }

    private String getResult(DefaultMutableTreeNode node){
    	String str="";
    	String tempStr="";
    	Object nodeInfo = node.getUserObject();
		Info info = (Info) nodeInfo;
    	if(node.getChildCount()==0){
    		return info.getNodeValue();
    		
    	} else {
    		tempStr = "(";
    		tempStr += getResult((DefaultMutableTreeNode) node.getFirstChild());
    		tempStr += info.getNodeValue();
    		tempStr += getResult((DefaultMutableTreeNode) node.getLastChild());
    		tempStr += ")";
    		
    		return str + tempStr; 		
    	}
    	
    }
    
    
    private void displayResult(String str){
    	try {
            if (str != null) {
            	if(str.charAt(0)=='(' && str.charAt(str.length()-1)==')'){
            		str = str.substring(1, str.length()-1);
            		
            	}
            	str = str+"";
                htmlPane.setText(str);
                
            } else { 
            	htmlPane.setText("File Not Found");
                if (DEBUG) {
                    System.out.println("Attempted to display a null URL.");
                }
            }
        } catch (Exception e) {
            System.err.println("Attempted to read a bad URL: " + str);
        }
    	
    }
    
    private void createNodes(DefaultMutableTreeNode top,BinaryTree bTree,String postFix) {

        
    	bTree.isVisit = true;
    
    	DefaultMutableTreeNode node = top;
    	DefaultMutableTreeNode parentNode = top;
    	
    	Stack<DefaultMutableTreeNode> stack = new Stack<DefaultMutableTreeNode>();
    	stack.push(parentNode);
        int i=0;	

        	
        while(i<postFix.length()-1){
    		if(bTree.leftNode==null && bTree.rightNode == null){
    			BinaryTree parent = new BinaryTree();
    			parent = bTree.parentNode;
    			bTree = parent;
    			stack.pop();
    			
    		} else if(bTree.leftNode!=null){
    			BinaryTree leftNode = bTree.leftNode;
    			BinaryTree rightNode = bTree.rightNode;
    			if(leftNode.isVisit==true && rightNode.isVisit==true){
    				BinaryTree parent = new BinaryTree();
        			parent = bTree.parentNode;
        			bTree = parent;
        			stack.pop();
    				
    			} else if(leftNode.isVisit==false){
    				leftNode.isVisit = true;
    				bTree = leftNode;
 
    				node = new DefaultMutableTreeNode(new Info(leftNode.value));
    				parentNode = stack.peek();
    				parentNode.add(node);
    				parentNode = node;
    				stack.push(parentNode);
    				
        			i++;
        		} else if(rightNode.isVisit==false){
        			rightNode.isVisit = true;
        			bTree = rightNode;
        			node = new DefaultMutableTreeNode(new Info(rightNode.value));
        			parentNode = stack.peek();
    				parentNode.add(node);
        			parentNode = node;
        			stack.push(parentNode);
        			i++;
        			
        		}
        	} 
        }
    }


    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = TreeCalculator.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }


    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        TreeCalculator newContentPane = new TreeCalculator();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
    	System.out.print("Input posfix number (251-*32*+) :");
    	Input = scanner.next();
    	
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	
                createAndShowGUI();
              
            }
        });
    }
    
    
	private static BinaryTree genTree(String postFix){
		int i;

		Stack<String> stack = new Stack<String>();
		String val = "";
		for(i=0;i<postFix.length();i++){
			stack.push(""+postFix.charAt(i));
			
		}
		
		BinaryTree tree = new BinaryTree();
		
		while(!stack.isEmpty()){
			val = stack.pop();
			if(tree.value==null){
				tree.value = val;
				tree.level = 0;
				
			} else {
				if(val.equals("+")||val.equals("-")||val.equals("*")||val.equals("/")){
					boolean complete = false;
					do{
						if(tree.rightNode==null){
							
							BinaryTree parent = new BinaryTree();						
							tree.rightNode = new BinaryTree();
							
							BinaryTree rightNode = tree.rightNode;
							rightNode.value = val;
							
							parent = tree;
							
							rightNode.parentNode = parent;
							rightNode.level = parent.level +1;
							
							tree = rightNode;
							
							complete = true;
							
							
						} else if(tree.leftNode==null) {
							BinaryTree parent = new BinaryTree();						
							tree.leftNode = new BinaryTree();
							
							BinaryTree leftNode = tree.leftNode;
							leftNode.value = val;
							
							parent = tree;
							
							leftNode.parentNode = parent;
							leftNode.level = parent.level +1;
							
							tree = leftNode;
							
							complete = true;
							
						} else {
							BinaryTree parent = new BinaryTree();
							if(tree.parentNode!=null){
								parent = tree.parentNode;
								tree = parent;
							}
							
						}
					}while(!complete);
					
				} else {
					boolean complete = false;
					do{
						if(tree.rightNode==null){
							BinaryTree rightNode = new BinaryTree();
							rightNode.value = val;
							rightNode.parentNode = tree;
							rightNode.level = tree.level+1;
							
							tree.rightNode = rightNode;
							
							complete = true;
							
							
						} else if(tree.leftNode==null) {
							BinaryTree leftNode = new BinaryTree();
							leftNode.value = val;
							leftNode.parentNode = tree;
							leftNode.level = tree.level +1;
							
							tree.leftNode = leftNode;
							
							complete = true;
							
						} else {
							BinaryTree parent = new BinaryTree();
							if(tree.parentNode!=null){
								parent = tree.parentNode;
								tree = parent;
							}
							
							
						}
					}while(!complete);
					
					
				}
				
				
			}
			
			
			
		}
		
		while(tree.parentNode!=null){
			BinaryTree parent = new BinaryTree();
			parent = tree.parentNode;
			tree = parent;
			
		}
	
		
		
		return tree;
	}
	
 
}

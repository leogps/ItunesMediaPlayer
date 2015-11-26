package com.gps.itunes.media.player.ui.fileutils;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

/**
 * Created by leogps on 10/10/15.
 */
public class FileNode implements TreeNode {

    private static final FileFilter NON_HIDDEN_FILE_FILTER = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return !file.isHidden();
        }
    };

    private final Map<FileNode, Integer> childIndexMap = new HashMap<FileNode, Integer>();

    private final File file;

    public File getFile() {
        return file;
    }

    public FileNode(File file) {
        this.file = file;
    }

    private List<FileNode> cachedChildren;// TODO: use timestamp and refresh if queried after sometime.


    public TreeNode getChildAt(int i) {
        if(file.isDirectory()) {
            List<FileNode> children = getChildren();
            if(i < children.size()) {
                return getChildren().get(i);
            }
        }
        return null;
    }

    public int getChildCount() {
        if(file.isDirectory()) {
            return getChildren().size();
        }
        return 0;
    }


    public TreeNode getParent() {
        return new FileNode(file.getParentFile());
    }


    public int getIndex(TreeNode treeNode) {
        if(file.isDirectory() && treeNode instanceof FileNode) {
            FileNode fileNode = (FileNode) treeNode;
            if (cachedChildren == null) {
                buildChildIndex();
            }
            if (cachedChildren.contains(treeNode)) {
                return childIndexMap.get(fileNode);
            }
        }
        return -1;
    }

    private void buildChildIndex() {
        getChildren();
    }

    public boolean getAllowsChildren() {
        return file.isDirectory() && file.canWrite();
    }


    public boolean isLeaf() {
        return !file.isDirectory();
    }


    public Enumeration children() {
        if(!file.isDirectory()) {
            return new Vector().elements();
        }
        Enumeration<FileNode> enumeration = new Vector<FileNode>(getChildren()).elements();
        return enumeration;
    }

    public List<FileNode> getChildren() {
        if(cachedChildren == null) {
            cachedChildren = doRetrieveChildren();
        }
        return cachedChildren;
    }

    private List<FileNode> doRetrieveChildren() {
        if(file.isDirectory()) {
            File[] children = file.listFiles(NON_HIDDEN_FILE_FILTER);
            if(children != null) {
                List<FileNode> childNodeList = new ArrayList<FileNode>();
                for (int index = 0; index < children.length; index++) {
                    File child = children[index];
                    FileNode childNode = new FileNode(child);
                    childNodeList.add(childNode);
                    childIndexMap.put(childNode, index);
                }
                return childNodeList;
            }
        }
        return new ArrayList<FileNode>();
    }

    @Override
    public String toString() {
        return file.getName();
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(!(o instanceof FileNode)) {
            return false;
        }

        FileNode that = (FileNode) o;

        if(that.getFile() == null && this.getFile() == null) {
            return true;
        }
        if(that.getFile() != null && this.getFile() == null) {
            return false;
        }
        if(that.getFile() == null && this.getFile() != null) {
            return false;
        }

        try {
            return this.getFile().getCanonicalPath().equals(that.getFile().getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public TreePath getPathTo(File pathTo) throws IOException {
        String[] pathTokenArray = pathTo.getCanonicalPath().split(File.separator);

        List<FileNode> treePathList = new ArrayList<FileNode>();
        treePathList.add(this);

        FileNode parentNode = this;
        for(int i = 0; i < pathTokenArray.length; i++) {
            boolean found = false;
            List<FileNode> children = parentNode.getChildren();

            for(int j = 0; j < children.size(); j++) {
                String pathToken = pathTokenArray[i];
                FileNode fileNode = children.get(j);
                if(fileNode.getFile().getName().equals(pathToken)) {
                    found = true;
                    parentNode = fileNode;
                    treePathList.add(fileNode);

                    break;
                }
            }
            if(!found) {
                break;
            }
        }

        return new TreePath(treePathList.toArray(new FileNode[treePathList.size()]));
    }

}

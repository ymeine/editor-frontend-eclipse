Eclipse frontend plugin code, making use of the backend to provide source code edition tools.





# File system layout

- [`readme.md`](./readme.md): the main documentation file
- [`com/`](./com/): the root package of the Eclipse plugin code





# Documentation

There is nothing particular in this folder, as the Java conventions require a somehow deeply nested file system structure to organize packages: this is the concept of classpath.





# References

## Tutorials

- [Building an Eclipse Text Editor with JFace Text](http://www.realsolve.co.uk/site/tech/jface-text.php)
- [Create commercial-quality eclipse ide](http://www.ibm.com/developerworks/views/opensource/libraryview.jsp?search_by=Create+commercial-quality+eclipse+ide) on _IBM developerWorks : Open source :  Technical library_
	- [Create a commercial-quality Eclipse IDE, Part 2: The user interface](http://www.ibm.com/developerworks/opensource/tutorials/os-ecl-commplgin2/index.html)

## Eclipse documentation

[Home](http://help.eclipse.org/)

- `org.eclipse.ui.texteditor.AbstractTextEditor`, `createPartControl`, `getAdapter`
- `org.eclipse.ui.editors.text.FileDocumentProvider`
- `org.eclipse.ui.views.contentoutline.ContentOutlinePage`, `getTreeViewer`
- `org.eclipse.jface.viewers.StructuredViewer`, `setInput`
- `org.eclipse.jface.viewers.TreeNodeContentProvider`, `getElements`
- `org.eclipse.jface.viewers.TreeNode`

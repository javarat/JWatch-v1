//
//public class createJar {
//	
//	private void createJar() throws IOException {
//		        String mainStr = txtMain.getText();
//		        String authorStr = txtAuthor.getText();
//		        String classesStr = txtAll.getText();
//		        if ((mainStr.length() < 1) || (classesStr.length() < 1)) {
//		            JOptionPane.showMessageDialog(null,
//		                    "Please complete 'Main Class' and 'Classes' fields");
//		        } else {
//		            boolean failed = false;
//		            File fileMft = new File("manifest.mf");
//		            FileWriter fwr = new FileWriter(fileMft);
//		            try {
//		                if ((fileMft.exists()) && (fileMft.isFile())) {
//		                    fileMft.delete();
//		                }
//		                fileMft.createNewFile();
//		                fwr.write("Manifest-Version: 1.0\n" + "Main-Class: " + mainStr + "\nCreated-By: " + authorStr + "\n");
//		                fwr.close();
//		            } catch (IOException x) {
//		                failed = true;
//		            }
//		            try {
//		                Runtime.getRuntime().exec("jar cmf manifest.mf " + mainStr + ".jar " + classesStr + " *.gif *.jpg");
//		            } catch (IOException x) {
//		                failed = true;
//		            }
//		            if (failed) {
//		                JOptionPane.showMessageDialog(null, "Jar creation failed for unknown reason(s)", "JarCreator", JOptionPane.ERROR_MESSAGE);
//		            } else {
//		                JOptionPane.showMessageDialog(null, "Jar creation successful: click on " + mainStr + ".jar to test the result", "JarCreator", JOptionPane.INFORMATION_MESSAGE);
//		            }
//		        }
//		    }
//
//}

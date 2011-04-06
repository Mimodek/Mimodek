final JFileChooser fc = new JFileChooser(); 

void openDialog(){
 /* try { 
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
  } 
  catch (Exception e) { 
    e.printStackTrace();  

  }
  // in response to a button click: 
  int returnVal = fc.showOpenDialog(this); 

  // in response to a button click: 
  if (returnVal == JFileChooser.APPROVE_OPTION) { 
    File file = fc.getSelectedFile(); 
    // see if it's an image 
    // (better to write a function and check for all supported extensions) 
    if (file.getName().endsWith("txt")) { 
      // load the image using the given file path
      cRange.loadFromFile(file.getPath());
    } 
    else { 
      // just print the contents to the console 
      // note: loadStrings can take a Java File Object too 
      String lines[] = loadStrings(file); 
      for (int i = 0; i < lines.length; i++) { 
        println(lines[i]);  
      } 
    } 
  } 
  else { 
    println("Open command cancelled by user."); 
  } */
  cRange.loadFromFile(pathToColorRangeFile);
}

void saveDialog(){
  /*
  try { 
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
  } 
  catch (Exception e) { 
    e.printStackTrace();  

  }
  // in response to a button click: 
  int returnVal = fc.showSaveDialog(this); 

  // in response to a button click: 
  if (returnVal == JFileChooser.APPROVE_OPTION) { 
    File file = fc.getSelectedFile(); 
    // see if it's an image 
    // (better to write a function and check for all supported extensions) 
    if (file.getName().endsWith("txt")) { 
      // load the image using the given file path
      cRange.saveToFile(file.getPath());
      // cRange.loadFromFile(file.getPath());
    } 
  } 
  else { 
    println("Open command cancelled by user."); 
  }*/
  cRange.saveToFile(pathToColorRangeFile);
}





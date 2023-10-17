package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.*;
import com.github.aklakina.edmma.machineInterface.FileReader;

import java.util.TreeMap;

@Singleton
public class DataStorage {

    private static TreeMap<FileData, FileReader> fileReaders = new TreeMap<>();



}

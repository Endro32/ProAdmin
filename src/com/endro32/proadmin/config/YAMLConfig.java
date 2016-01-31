package com.endro32.proadmin.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import org.yaml.snakeyaml.Yaml;

import com.endro32.proadmin.util.FileManager;

public class YAMLConfig {
	Yaml yaml;
	
	File config;
	
	public YAMLConfig(String path, boolean abs) {
		yaml = new Yaml();
		if(abs) {
			config = new File(path);
		} else {
			config = new File(FileManager.appdir+"/"+path);
		}
	}
	
	public Object getValue(String key) throws FileNotFoundException {
		InputStream input = new FileInputStream(config);
		Object value = yaml.load(input);
		String[] tree = key.split(Pattern.quote("."));
		for(String i : tree) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) value;
			value = map.get(i);
		}
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public void setValue(String key, Object data) throws IOException {
		InputStream input = new FileInputStream(config);
		String[] tree = key.split(Pattern.quote("."));
		Stack<Map<String, Object>> maps = new Stack<Map<String, Object>>();
		maps.push((Map<String, Object>) yaml.load(input));
		for(int i = 0; i < tree.length-1; i++) {
			maps.push((Map<String, Object>) maps.elementAt(i).get(tree[i]));
		}
		maps.lastElement().put(tree[tree.length-1], data);
		BufferedWriter out = new BufferedWriter(new FileWriter(config));
		out.write(yaml.dumpAsMap(maps.get(0)));
		out.close();
	}
	
	@SuppressWarnings("unchecked")
	public boolean addToList(String key, String value) throws IOException {
		List<Object> list = (List<Object>) getValue(key);
		list.add(value);
		setValue(key, list);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean removeFromList(String key, String value) throws IOException {
		List<Object> list = (List<Object>) getValue(key);
		if(!list.contains(value)) return false;
		list.remove(value);
		setValue(key, list);
		return true;
	}
	
}

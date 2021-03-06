package utility;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import net.barenca.jastyle.ASFormatter;
import net.barenca.jastyle.FormatterHelper;

public class MyTokenizer {

	String itemToTokenize;
	public MyTokenizer(String item)
	{
		//initialization
		this.itemToTokenize=item;
	}
	
	public ArrayList<String> tokenize_text_item()
	{
		//tokenizing textual content
		StringTokenizer tokenizer = new StringTokenizer(this.itemToTokenize);
		ArrayList<String> tokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			token.trim();
			if (!token.isEmpty()) {
				tokens.add(token);
			}
		}
		return tokens;
	}
	
	public ArrayList<String> tokenize_stack_item()
	{
		//code for tokenization of stack trace
		String tstack=format_the_stacktrace(this.itemToTokenize);
		StringTokenizer tokenizer = new StringTokenizer(tstack);
		ArrayList<String> tokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			token.trim();
			if (!token.isEmpty()) {
				tokens.add(token);
			}
		}
		return tokens;
	}
	
	public ArrayList<String> tokenize_code_item()
	{
		//code for tokenization of code elements
		String tcode=format_the_code(this.itemToTokenize);
		String fcode=remove_code_comment(tcode);
		StringTokenizer tokenizer = new StringTokenizer(fcode);
		ArrayList<String> tokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			token.trim();
			if (!token.isEmpty()) {
				ArrayList<String> tokenparts=process_source_token(token);
				tokens.addAll(tokenparts);
			}
		}
		return tokens;
	}
	
	protected static String remove_code_comment(String codeFragment)
	{
		//code for removing code fragment
		String modifiedCode = new String();
		try {
			String pattern = "//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/";
			modifiedCode = codeFragment.replaceAll(pattern, "");
		} catch (Exception exc) {
		}catch(StackOverflowError err){
			
		}
		return modifiedCode;
	}
	
	protected static String format_the_code(String codeFragment)
	{
		//code for formatting code fragment
		ASFormatter formatter=new ASFormatter();
		Reader in=new BufferedReader(new StringReader(codeFragment));
		formatter.setJavaStyle();
		String formattedCode=FormatterHelper.format(in, formatter);
		return formattedCode;
	}
	
	protected static String format_the_stacktrace(String stackTrace)
	{
		//code for formatting stack trace
		String delim="\\s+at";
		String[] lines=stackTrace.split(delim);
		String modified_stack_trace=new String();
		for(String line:lines)
		{
			String modified_line=line;//process_stack_line(line);
			modified_stack_trace+=modified_line+"\n";
		}
		//returning formatted stack
		return modified_stack_trace;
	}
	
	protected static String process_stack_line(String line)
	{
		//code for processing the line
		String modified=new String();
		String[] parts=org.apache.commons.lang.StringUtils.splitByCharacterTypeCamelCase(line);
		for (String part : parts) {
			String[] segments = part.split("\\.");
			if (segments.length == 0)
				modified +=" "+part;
			else {
				for (String segment : segments) {
					modified+=" "+segment;
				}
			}
		}
		return modified.trim();
	}
	
	protected static ArrayList<String> process_source_token(String token)
	{
		//code for processing source code token
		ArrayList<String> modified=new ArrayList<String>();
		String[] segments = token.split("\\.");
		for(String segment:segments)
		{
			String[] parts=org.apache.commons.lang.StringUtils.splitByCharacterTypeCamelCase(segment);
			if(parts.length==0)
			{
				modified.add(segment);
			}else
			{
				for(String part:parts)
				{
					modified.add(part);
				}
			}
		}
		return modified;
	}
	
	
	
	
	
	
	
	
	
	
}

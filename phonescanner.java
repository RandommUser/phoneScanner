import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;


public class phonescanner {
	public static void main(String[] args)
	{
		/*
			Go thru the given websites as args
		*/
		int i;
		for (i = 0; i < args.length; i++)
		{
			System.out.println(args[i] + ":");
			try
			{
				String[] matches = parseWebsite.getSource(args[i]);
				/*
					Remove duplicated matches
				*/
				matches = Arrays.stream(matches).distinct().toArray(String[]::new);
				if (matches.length > 0)
				{
					for (String str : matches)
					{
						System.out.println(phoneMatch.numType(str));
					}
					System.out.println(matches.length + " unique number(s) found");
				}
				else
				{
					System.out.println("No numbers found");
				}
			}
			catch (Exception IOException)
			{
				System.out.println("Failed to parse:" + args[i]);
				System.out.println(IOException.getMessage());
			}
		}
		if (i == 0)
		{
			System.out.println("Pass the website(s) as parameter(s).");
		}
		return ;
	}
}

class parseWebsite {

	static String[] getSource(String input) throws IOException
	{
		List<String> numbers = new ArrayList<String>();
		/*
			Get the webpage
		*/
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet getReq = new HttpGet(input);
		CloseableHttpResponse getResp = httpclient.execute(getReq);
		Scanner reader = new Scanner(getResp.getEntity().getContent());
		StringBuffer buffer = new StringBuffer();
		/*
			Read the page to the buffer
		*/
		while(reader.hasNextLine())
		{
			buffer.append(reader.nextLine() + "\n");
		}
		/*
			Send the buffer to be cleaned of HTML tags
			and then to be matched against the regex
		*/
		List<String> found = phoneMatch.findNum(cleanHTMLInput.cleanString(buffer.toString()));
		for (String str : found)
		{
			numbers.add(str);
		}
		reader.close();
		httpclient.close();
		String[] ret = new String[numbers.size()];
		return (numbers.toArray(ret));
	}
}

class phoneMatch {
	/*
		Use regex to match for a phone number
		Uses few different ways to indicate the phone number
		+358 ...
		(358) ...
		+358 (0) ...
		+358....
		0123....
	*/
	static String phoneRegex = "(\\(?\\d{1,3}\\)?\\s?|(\\+\\d{1,3}\\s))?(\\(\\d{1,3}\\)\\s?)?\\d{1,3}[\\s-]?\\d{3,4}[\\s-]?\\d{3,4}";
	/*
		Typical first character in a phone number
	*/
	static String goodStart = "^[\\+(0]";

	static List<String> findNum(String input)
	{
		List<String> matches = new ArrayList<String>();
		Pattern pat = Pattern.compile(phoneRegex);
		Matcher matcher = pat.matcher(input);
		while(matcher.find())
		{
			matches.add(matcher.group(0));
		}
		return (matches);
	}

	/*
		Some numbers are a bit unique. This function adds an warning that the number
		might not be a valid one. You could be more specific in the number regex,
		like have a set formatting for the local numbers, but this
		lets you be more flexible to find non-typical numbers.
	*/
	static String numType(String input)
	{
		Pattern pat = Pattern.compile(goodStart);
		Matcher matcher = pat.matcher(input);
		if (matcher.find())
		{
			return (input);
		}
		return (input + " (possible false-positive)");
	}
}

class cleanHTMLInput {

	/*
		Regex to catch mostly HTML tags to remove possible false matches
	*/
	static String htmlTags = "(<[\\s\\S]+?>)";
	static String scriptTags = "(<script.+?>[\\s\\S]+?</script>)";
	static String headTag = "(<head>[\\s\\S]+?</head>)";
	static String iframeTag = "(<iframe.+?>[\\s\\S]+?</iframe>)";

	static String cleanString(String input)
	{
		/*
		Clean the read input from website from the HTML tags and text inside
		Makes it easier to parse the data for phone numbers
		*/
		input = input.replaceAll(headTag, "\n");
		input = input.replaceAll(iframeTag, "\n");
		input = input.replaceAll(scriptTags, "\n");
		input = input.replaceAll(htmlTags, "\n");
		return (input);
	}
}

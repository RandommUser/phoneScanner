# phoneScanner
Java HTTP client that parses webpages for phone numbers.

## How it works?

The program takes in url(s) as parameters. It then makes a connection to the webpage. It parses the content into a single string, which gets passed to be processed. The page content gets cleaned of most HTML tags to make it easier to parse the actual content on the page.

Next the remaining data is matched against a phonenumber regex. Matched results then get printed out on the console.

## How to run

Made on Java 17.0.1.
Apache libraries used are located in the lib-folder.

**MacOS:**

Run the run.sh file. Add the url(s) as parameters. Tested Using Bash 3.2

**Windows:**

Run the run.bat file. Add the url(s) as parameters. Testing using Windows 10


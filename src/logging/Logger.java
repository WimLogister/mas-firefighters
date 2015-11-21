package logging;

public class Logger {

  public static boolean DEBUG_OUTPUT_CALL_INFO = true;

  /**
   * Used to produce a String formatted according to {@link String#format(String, Object...)}.
   * 
   * @param format
   * @param args
   * @return
   */
  public static final String format(String format, Object... args) {

    return String.format(format, args);
  }

  /**
   * Adds the call information to the current message (Where called from (Class, Method, Linenumber)
   * 
   * @param message
   * @return
   */
  private static final String concatCallInfo(String message) {
    return String.format("%-60s : %s", getCallInfo(4), message);
  }

  /**
   * 
   * @param depth
   * @return String representing call information from the stack at a specified depth
   */
  public static String getCallInfo(int depth) {
    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

    if (depth >= stackTraceElements.length) {
      return "Requested depth outside scope";
    }

    String callInfo = null;

    StackTraceElement previousMethod = stackTraceElements[depth];

    if (previousMethod == null) {
      return "Strange call, cannot find caller";
    }

    String fileName = previousMethod.getFileName();
    String methodName = previousMethod.getMethodName();
    int lineNumber = previousMethod.getLineNumber();

    fileName = fileName == null ? "unknown" : fileName;
    methodName = methodName == null ? "unknown" : methodName;

    callInfo = String.format("%s::%s:%d", fileName.replace(".java", ""), methodName, lineNumber);
    return callInfo;
  }

  public static final void println(String message) {
    if (DEBUG_OUTPUT_CALL_INFO)
      message = concatCallInfo(message);

    System.out.println(message);
  }

}

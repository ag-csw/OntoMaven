import org.apache.maven.plugin.logging.Log;

public class StringBufferLog implements Log {

	private StringBuilder sb = new StringBuilder(  );

	public String getLogData() {
		return sb.toString();
	}

	@Override
	public boolean isDebugEnabled() {
		return true;
	}

	@Override
	public void debug( CharSequence charSequence ) {
		sb.append( "[debug] " ).append( charSequence ).append( "\n" );
	}

	@Override
	public void debug( CharSequence charSequence, Throwable throwable ) {
		sb.append( "[debug] " ).append( charSequence ).append( "\n" ).append( "\t\t" ).append( throwable.getMessage() ).append( "\n" );
	}

	@Override
	public void debug( Throwable throwable ) {
		sb.append( "[debug] " ).append( "\t\t" ).append( throwable.getMessage() ).append( "\n" );
	}

	@Override
	public boolean isInfoEnabled() {
		return true;
	}

	@Override
	public void info( CharSequence charSequence ) {
		sb.append( "[info] " ).append( charSequence ).append( "\n" );
	}

	@Override
	public void info( CharSequence charSequence, Throwable throwable ) {
		sb.append( "[info] " ).append( charSequence ).append( "\n" ).append( "\t\t" ).append( throwable.getMessage() ).append( "\n" );
	}

	@Override
	public void info( Throwable throwable ) {
		sb.append( "[info] " ).append( "\t\t" ).append( throwable.getMessage() ).append( "\n" );
	}

	@Override
	public boolean isWarnEnabled() {
		return true;
	}

	@Override
	public void warn( CharSequence charSequence ) {
		sb.append( "[warn] " ).append( charSequence ).append( "\n" );
	}

	@Override
	public void warn( CharSequence charSequence, Throwable throwable ) {
		sb.append( "[warn] " ).append( charSequence ).append( "\n" ).append( "\t\t" ).append( throwable.getMessage() ).append( "\n" );
	}

	@Override
	public void warn( Throwable throwable ) {
		sb.append( "[warn] " ).append( "\t\t" ).append( throwable.getMessage() ).append( "\n" );
	}

	@Override
	public boolean isErrorEnabled() {
		return true;
	}

	@Override
	public void error( CharSequence charSequence ) {
		sb.append( "[error] " ).append( charSequence ).append( "\n" );
	}

	@Override
	public void error( CharSequence charSequence, Throwable throwable ) {
		sb.append( "[true] " ).append( charSequence ).append( "\n" ).append( "\t\t" ).append( throwable.getMessage() ).append( "\n" );
	}

	@Override
	public void error( Throwable throwable ) {
		sb.append( "[error] " ).append( "\t\t" ).append( throwable.getMessage() ).append( "\n" );
	}
}

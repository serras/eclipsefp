package net.sf.eclipsefp.haskell.browser.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import net.sf.eclipsefp.haskell.browser.items.Declaration;
import net.sf.eclipsefp.haskell.browser.items.HaskellPackage;
import net.sf.eclipsefp.haskell.browser.items.Module;
import net.sf.eclipsefp.haskell.browser.items.PackageIdentifier;
import net.sf.eclipsefp.haskell.browser.items.Packaged;

import org.eclipse.core.runtime.IPath;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used for communicating with a Scion Browser instance.
 * 
 * @author serras
 */

public class StreamBrowserServer extends BrowserServer {

	private IPath serverExecutable;
	private Process process = null;
	private BufferedWriter in = null;
	private BufferedReader out = null;

	public StreamBrowserServer(IPath serverExecutable) throws Exception {
		this.serverExecutable = serverExecutable;
		startServer();
	}

	public void startServer() throws Exception {
		ProcessBuilder builder = new ProcessBuilder(serverExecutable.toOSString());
		builder.redirectErrorStream(true);

		try {
			process = builder.start();
			out = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF8"));
			in = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), "UTF8"));
		} catch (Throwable ex) {
			throw new Exception("Could not load");
		}
	}

	public String sendAndReceive(JSONObject input) throws IOException {
		String jsonInput = input.toString();
		log(">> " + jsonInput);
		in.write(jsonInput + "\n");
		in.flush();
		String response = out.readLine();
		log(response);
		return response;
	}

	public void sendAndReceiveOk(JSONObject input) throws IOException {
		String jsonInput = input.toString();
		log(">> " + jsonInput);
		in.write(jsonInput + "\n");
		in.flush();

		String response = null;
		do {
			response = out.readLine();
			log(response);
		} while (!response.equals("\"ok\""));
	}

	@Override
	public void loadLocalDatabase(String path, boolean rebuild) throws IOException, JSONException {
		sendAndReceiveOk(Commands.createLoadLocalDatabase(path, rebuild));
	}

	@Override
	public void setCurrentDatabase(CurrentDatabase current, PackageIdentifier id)
			throws IOException, JSONException {
		sendAndReceiveOk(Commands.createSetCurrentDatabase(current, id));
	}

	@Override
	public HaskellPackage[] getPackages() throws IOException, JSONException {
		String response = sendAndReceive(Commands.createGetPackages());
		return Commands.responseGetPackages(response);
	}

	@Override
	public Module[] getAllModules() throws IOException, JSONException {
		String response = sendAndReceive(Commands.createGetAllModules());
		return Commands.responseGetModules(response);
	}

	@Override
	public Module[] getModules(String module) throws IOException, JSONException {
		String response = sendAndReceive(Commands.createGetModules(module));
		return Commands.responseGetModules(response);
	}

	@Override
	public Packaged<Declaration>[] getDeclarations(String module) throws Exception {
		String response = sendAndReceive(Commands.createGetDeclarations(module));
		return Commands.responseGetDeclarations(response);
	}
}

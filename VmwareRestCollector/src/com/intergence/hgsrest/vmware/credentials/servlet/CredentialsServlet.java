package com.intergence.hgsrest.vmware.credentials.servlet;

import com.google.common.base.Strings;
import com.intergence.hgsrest.vmware.credentials.Credential;
import com.intergence.hgsrest.vmware.credentials.VmWareCredentialRepository;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * Very crude servlet for allowing embedded web page to manage the vmware credentials
 *
 * @author Lake
 */
public class CredentialsServlet extends HttpServlet {

	private VmWareCredentialRepository vmWareCredentialRepository;

	private static final String style =
			"\t<style>\n" +
            "\t\tlabel {\n" +
            "\t\t\twidth: 8em;\n" +
            "\t\t\tdisplay: inline-block;\n" +
            "\t\t}\n" +
            "\t\t\n" +
            "\t\t.msg { height: 1em; }\n" +
            "\t\t\n" +
            "\t\ttable, td, th {\n" +
            "\t\t\tborder-spacing: 0;\n" +
            "\t\t\tborder-collapse: collapse;\n" +
            "\t\t\tborder-width: 1px;\n" +
            "\t\t\tborder-style: solid;\n" +
			"\t\t\tpadding: 2;\n" +
            "\t\t}\n" +
            "\t\t\n" +
            "\t\ttable {\n" +
            "\t\t\twidth: 30em;\n" +
            "\t\t}\n" +
            "\t\t\n" +
            "\t\tform {\n" +
            "\t\t\tmargin: 0;\n" +
            "\t\t}\n" +
            "\t</style>";

	private static final String form =
			"\t<form name=\"add-node\" action=\"add\" method=\"POST\">\n" +
            "\t\t<label for=\"username\">Username:</label>\n" +
            "\t\t<input type=\"text\" name=\"username\" id=\"username\" size=\"30\" value=\"\" /><br />\n" +
            "\t\t<label for=\"password\">Password:</label>\n" +
            "\t\t<input type=\"password\" name=\"password\" id=\"password\" size=\"30\" value=\"\" /><br />\n" +
            "\t\t<label for=\"hostNameOrIp\">IP Address / Name:</label>\n" +
            "\t\t<input type=\"text\" name=\"hostNameOrIp\" id=\"hostNameOrIp\" size=\"30\" value=\"\" /><br />\n" +
            "\t\t<input type=\"submit\" name=\"submit\" id=\"submit\" value=\"Submit\" />\n" +
            "\t</form>";


	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if ("/add".equals(request.getServletPath())) {
			addCredential(request, response);
            return;
		}
		else if ("/delete".equals(request.getServletPath())) {
			deleteCredential(request, response);
            return;
		}
        else {
            buildPage(response);
        }
    }

	private void buildPage(HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		ServletOutputStream out = response.getOutputStream();
		out.println("<html>");
		out.println("<head>");
		out.println(style);
		out.println("</head>");
		out.println("<body>");
		out.println("\t<h1>VmWare Credential Entry</h1>\n");
		out.println(form);
		out.println("<div class=\"msg\" id=\"success-msg\"></div>");
		out.println("<h2>All Credentials:</h2>");
		out.println("<table>");
		out.println("<thead><tr><th>IP Address / Name</th><th>Username</th><th>Delete</th></tr></thead>");
		out.println("<tbody>");

		Collection<Credential> all = vmWareCredentialRepository.findAll();
		for (Credential credential : all) {
			out.println("<tr>");
			out.println("\t<td>" + credential.getHostNameOrIp() + "</td>");
			out.println("\t<td>" + credential.getUsername() + "</td>");

			out.println("\t<td >");
			out.println("\t\t<form name=\"delete-node\" action=\"delete\" method=\"POST\">");
			out.println("\t\t<input type=\"submit\" name=\"submit\" id=\"submit\" value=\"Delete\" />");
			out.println("\t\t<input type=\"hidden\" name=\"key\" id=\"key\" value=\"" + credential.getKey()+ "\" />");
			out.println("\t\t</form>");
			out.println("\t</td>");
			out.println("</tr>");
		}
		out.println("</tbody>");
		out.println("</body>");
		out.println("</html>");
		out.flush();
	}

	private void deleteCredential(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String key = request.getParameter("key");
		if (!Strings.isNullOrEmpty(key)) {
			vmWareCredentialRepository.deleteCredential(Integer.parseInt(key));
		}

        String urlWithSessionID = response.encodeRedirectURL("/");
        response.sendRedirect( urlWithSessionID );
    }

	private void addCredential(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String hostNameOrIp = request.getParameter("hostNameOrIp");
		if (!Strings.isNullOrEmpty(username) &&
				!Strings.isNullOrEmpty(password) &&
				!Strings.isNullOrEmpty(hostNameOrIp)) {
			vmWareCredentialRepository.putCredential(username, password, hostNameOrIp);
		}

        String urlWithSessionID = response.encodeRedirectURL("/");
        response.sendRedirect( urlWithSessionID );
    }

	public void setVmWareCredentialRepository(VmWareCredentialRepository vmWareCredentialRepository) {
		this.vmWareCredentialRepository = vmWareCredentialRepository;
	}
}

package edu.neu.csye7200.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

import edu.neu.csye7200.util.Driver;

/**
 * Servlet implementation class NewApplication
 */
@WebServlet(description = "NewApplication", urlPatterns = { "/NewApplication" })
public class NewApplication extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NewApplication() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.setContentType("text/html");
		
		
		
		response.setContentType("text/html");
		Driver driver = new Driver();
		try {
		java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("Fligt_date")); 
		String Response = driver.GetResponse(request.getParameter("FlightNumber"),date,
					request.getParameter("FlightOrigin"),
					request.getParameter("FlightDestination"),
					request.getParameter("OriginRain"),
					request.getParameter("OriginSnow"),
					request.getParameter("DestRain"),
					request.getParameter("DestSnow")
					);
			request.setAttribute("Reposne", Response);
		} catch (JSONException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 RequestDispatcher rd = request.getRequestDispatcher("Next.jsp");
		    rd.forward(request,response);
		  
		    
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

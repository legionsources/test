package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ManoServletas extends HttpServlet {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connection dbcon;  

    /**
     * DB setupas.
     */
    public void init(ServletConfig config) throws ServletException
    {
        String loginUser = "postgres";
        String loginPasswd = "postgres";
        String loginUrl = "jdbc:postgresql://localhost:5432/testdb";

        // Load the PostgreSQL driver
        try 
        {
              Class.forName("org.postgresql.Driver");
              dbcon = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        }
        catch (ClassNotFoundException ex)
        {
               System.err.println("ClassNotFoundException: " + ex.getMessage());
               throw new ServletException("Class not found Error");
        }
        catch (SQLException ex)
        {
               System.err.println("SQLException: " + ex.getMessage());
        }
    }
    
    /**
     * 
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    	
    	String whatToDo = request.getParameter("getButton");
    	
    	if (whatToDo != null) {
    		populateModel(request, response);
    		response.sendRedirect("index");
		} else {
			PrintWriter out= response.getWriter();
			// uzsetinu i ka rasysim (siuo atveju json objekta, kuri ajax parsitemps)
	    	response.setContentType("application/json;charset=utf-8");
	    	
	    	List<ModelObj> data = getUserList(request, response);
	    	
	        JSONObject jObject = new JSONObject();
	        try {
	                JSONArray jArray = new JSONArray();
	                for (ModelObj obj : data) {
	                    JSONObject userJSON = new JSONObject();
	                    userJSON.put("1", obj.getId());
	                    userJSON.put("2", obj.getFullName());
	                    userJSON.put("3", obj.getScreenName());
	                    userJSON.put("4", obj.getEmail());
	                    jArray.put(userJSON);
	                }
	                jObject.put("userList", jArray);
	        } catch (JSONException e) {
	            System.out.println("RAGAI");
	            e.printStackTrace();
	        }
	  
	        out.print(jObject);
		}
    }

    /**
     * 
     */
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
		
		String whatToDo = request.getParameter("dellButton");
	
		if (whatToDo != null) {
			String value = request.getParameter("dell");
			if (value != null) {
				deleteUser(value, request, response);
			}
		} else {
			
			long id = new Date().getTime();
			String fullName = request.getParameter("fullName");
			String screenName = request.getParameter("screenName");
			String email = request.getParameter("email");
			Statement statement = null;
			try
	        {
	                // Declare our statement
	                statement = dbcon.createStatement();
	                
	                String query = "INSERT INTO users (id, full_name, screen_name, email) ";
	                       query += "VALUES (" + id + ", '" + fullName + "', '" + screenName + "', '" + email + "')";
	                       
	                       System.out.println(query);
	                // Perform the query
	                statement.executeUpdate(query);
	        }
	        catch(Exception ex) {
	        	request.getSession().setAttribute("error", "ERRORAS: " + ex.getMessage());
	        	System.out.println("Erroras: " + ex.getMessage());
	        	response.sendRedirect("error");
	            return;
	        } finally {
	            try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	        }
			System.out.println("Duomenis irasyti sekmingai");
		}
		populateModel(request, response);
		response.sendRedirect("index");
		
    }
	
	/**
	 * Trinam useri.
	 * @param id
	 * @param request
	 * @param response
	 */
	private void deleteUser(final String id, HttpServletRequest request, HttpServletResponse response) {
		Statement statement = null;
		try
        {
			statement = dbcon.createStatement();
            String query = "DELETE FROM users WHERE id=" + id;    // OK cia sh, bo gali sql akata iraut.
            statement.executeUpdate(query);
        }
        catch(Exception ex) {
        	request.getSession().setAttribute("error", "ERRORAS: " + ex.getMessage());
        	System.out.println("Erroras: " + ex.getMessage());
        	try {
				response.sendRedirect("error");
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
            return;
        } finally {
            try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
	}
	
	/**
	 * Gauna Lista useriu is DB
	 * @param request
	 * @param response
	 * @return
	 */
	private List<ModelObj> getUserList(HttpServletRequest request, HttpServletResponse response) {
		List<ModelObj> model = new ArrayList<ModelObj>();
		Statement statement = null;
		
		HttpSession session = request.getSession();
		// trinam lista is sesijos
		session.removeAttribute("dataList");
    	
    	try
        {
    		statement = dbcon.createStatement();
            String query = "SELECT id, screen_name, full_name, email FROM users";

            // perform
            ResultSet rs = statement.executeQuery(query);

            // populate list
            long id;
            String fullName;
            String screenName;
            String email;
            
            while (rs.next()) {
            	id = rs.getLong("id");
                fullName = rs.getString("full_name");
                screenName = rs.getString("screen_name");
                email = rs.getString("email");
                model.add(new ModelObj(id, fullName, screenName, email));
            }
                
            statement.close();
        }
        catch(Exception ex) {
            System.out.println("Erroras: " + ex.getMessage());
            return null ;
            
        } finally {
        	try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
    	
    	return model;
	}
   
	/**
	 * Sesijos modeli paupdatina.
	 * @param request
	 * @param response
	 */
    private void populateModel(HttpServletRequest request, HttpServletResponse response) {
		List<ModelObj> model = getUserList(request, response);
		request.getSession().setAttribute("dataList", model);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.google.gson.Gson;
import dao.BillDAO;
import entities.Bill;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author carlo
 */
@WebServlet(name = "BillServlet", urlPatterns = {"/Bill"})
public class BillServlet extends HttpServlet {
    BillDAO bDao;
    
    
    @Override
    public void init(){
        bDao = new BillDAO();
    }
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void service (HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        String action = request.getParameter("action");
        Gson gson = new Gson();
        
        if (action.equals("show")){
            List<Bill> list = bDao.getUnpaidBills();
            String res = gson.toJson(list);
            try {
                PrintWriter pw = response.getWriter();
                pw.print(res);
            } catch (IOException ex) {
                Logger.getLogger(PaymentOrderServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
    }


}

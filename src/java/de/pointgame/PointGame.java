/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pointgame;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author tim
 */
@WebServlet(name = "PointGame", urlPatterns = {"/PointGame.do"}, asyncSupported = true)
public class PointGame extends HttpServlet {

    private static final Queue<AsyncContext> queue = new ConcurrentLinkedQueue<AsyncContext>();
    private static final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<String>();
    private Thread notifierThread = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        Runnable notifierRunnable = new Runnable() {

            public void run() {
                boolean done = false;
                while (!done) {
                    String cMessage = null;
                    try {
                        cMessage = messageQueue.take();
                        for (AsyncContext ac : queue) {
                            try {
                                PrintWriter acWriter = ac.getResponse().getWriter();
                                acWriter.println(cMessage);
                                acWriter.flush();
                            } catch (Exception e) {
                                System.out.println(e);
                                queue.remove(ac);
                            }
                        }
                    } catch (InterruptedException e) {
                        done = true;
                        System.out.println(e);
                    }
                }
            }
        };
        notifierThread = new Thread(notifierRunnable);
        notifierThread.start();
    }

    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "private");
        response.setHeader("Pragma", "no-cache");
        PrintWriter writer = response.getWriter();
        writer.write("doGet()");
        writer.flush();
        final AsyncContext ac = request.startAsync();
        ac.setTimeout(10 * 60 * 1000);
        ac.addListener(new AsyncListener() {

            public void onComplete(AsyncEvent event) throws IOException {
                queue.remove(ac);
            }

            public void onTimeout(AsyncEvent event) throws IOException {
                queue.remove(ac);
            }

            public void onError(AsyncEvent event) throws IOException {
                queue.remove(ac);
            }

            public void onStartAsync(AsyncEvent event) throws IOException {
            }
        });
        queue.add(ac);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "private");
        response.setHeader("Pragma", "no-cache");
        request.setCharacterEncoding("UTF-8");
        try {
            int px = Integer.parseInt(request.getParameter("px"));
            int py = Integer.parseInt(request.getParameter("py"));
            notify("" + px + ";" + py);
            response.getWriter().println("success");
        } catch (NumberFormatException e) {
            System.out.println(e);
        }

//        try {
//            out.println("{\"px\": \"" + px + "\",\"py\": \"" + py + "\"}");
//        } finally {
//            out.close();
//        }
    }

    @Override
    public void destroy() {
        queue.clear();
        notifierThread.interrupt();
    }

    private void notify(String cMessage) throws IOException {
        try {
            messageQueue.put(cMessage);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}

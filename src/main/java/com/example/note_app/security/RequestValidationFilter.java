//package com.example.note_app.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class RequestValidationFilter extends OncePerRequestFilter {
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String headerValue = request.getHeader("X-Valid-Request");
//        System.out.println("Inside RequestValidationFilter");
//        /*if(headerValue == null || !headerValue.equalsIgnoreCase("true")){
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid header sent");
//            response.getWriter().write("Invalid header sent");
//            return;
//        }*/
//        filterChain.doFilter(request, response);
//    }
//}

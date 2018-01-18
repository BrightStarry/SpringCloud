package com.zx.apigateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * author:ZhengXing
 * datetime:2018/1/18 0018 10:04
 */
@Component
public class CustomFilter extends ZuulFilter {
	//过滤器类型,此处pre表示在请求路由前.
	@Override
	public String filterType() {
		return "pre";
	}
	//路由器顺序,有多个时
	@Override
	public int filterOrder() {
		return 0;
	}
	//判断该过滤器是否需要执行.
	@Override
	public boolean shouldFilter() {
		return true;
	}
	//具体过滤逻辑
	@Override
	public Object run() {
		//获取当前请求上下文
		RequestContext context = RequestContext.getCurrentContext();
		//获取请求.
		HttpServletRequest request = context.getRequest();
		System.out.println("请求路径" + request.getRequestURL());

		try {
			//如果不包含token,就直接拦截该请求.
			if(StringUtils.isEmpty(ServletRequestUtils.getStringParameter(request,"token"))){
				//表示过滤该请求,不对其进行路由.
				context.setSendZuulResponse(false);
				//设置返回码
				context.setResponseStatusCode(401);
				//设置返回内容
				context.setResponseBody("xxx");
				return null;
			}

		} catch (ServletRequestBindingException e) {
			e.printStackTrace();
		}
		return null;
	}
}

package com.example.demo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.service.CheckDoneSimple;
import com.test.service.DeleteSimpleSet;
import com.test.service.FileDownload;
import com.test.service.FindFSimple;
import com.test.service.MannualaddFsimple;
import com.test.service.StartTestingController;
import com.test.service.XmlService;
import com.xml.standards.Standard;
import com.xml.standards.TestItem;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class ExcuteController {
	@Autowired
	private StartTestingController stc;
	@Autowired
	private MannualaddFsimple maf;
	@Autowired
	private CheckDoneSimple cds;
	@Autowired
	private DeleteSimpleSet dss;
	@Autowired
	private FileDownload fdl;
	@Autowired
	private FindFSimple ffs;
	
	 

	@GetMapping("/testing")

	public String excuteTesting(@RequestParam(value="batch", required=false) Integer batch,HttpServletRequest request,Model model) {

		 HttpSession session = request.getSession();
		 String name=(String) session.getAttribute("username");
		try {
			return stc.result(batch, model,name);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}

	@GetMapping("/testing/data")
	@ResponseBody
	public Map<String, Object> testingData(@RequestParam(value="batch", required=false) Integer batch,
			HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		String name = (String) session.getAttribute("username");
		Map<String, Object> result = new LinkedHashMap<>();
		try {
			String view = stc.result(batch, model, name);
			result.put("success", "testing".equals(view));
			result.put("view", view);
			result.put("presentList", model.asMap().get("presentList"));
			result.put("batchList", model.asMap().get("batchList"));
			result.put("recordPanel", model.asMap().get("recordPanel"));
			result.put("allSimpleNum", model.asMap().get("allSimpleNum"));
			result.put("doneSimpleNum", model.asMap().get("doneSimpleNum"));
			result.put("canOperate", model.asMap().get("canOperate"));
			result.put("currentUser", model.asMap().get("currentUser"));
		} catch (SQLException e) {
			result.put("success", false);
			result.put("message", e.getMessage());
		}
		return result;
	}
	
	@GetMapping("/questTesting")

	public String excuteQuestTesting(@RequestParam(value="batch", required=false) Integer batch,HttpServletRequest request,Model model) {

		 HttpSession session = request.getSession();
		 String name=(String) session.getAttribute("username");
		try {
			return stc.questResult(batch, model,name);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}

	@GetMapping("/manualAddFData")
	public String excuteManualAddFData(String simpleNum, String standardNumber, String simpleItem, String testMethord) {

		return maf.manualAddF(simpleNum, standardNumber, simpleItem, testMethord);
	}
	@GetMapping("/getStandard")
    @ResponseBody
    public List<String> getMethod() {

        // 返回检测方法
        return maf.getStandard();
    
}
	 @GetMapping("/getTestItems")
	    @ResponseBody
	    public List<String> getTestItemsByMethod(@RequestParam("method") String method) {
	       
	        // 返回匹配的检测项目
	        return maf.getTestItem(method);
	    
	}
	 @GetMapping("/getTestMethod")
	    @ResponseBody
	    public List<String> getTestMethodByItem(@RequestParam("method") String standardNumber,@RequestParam("testItem") String testItem) {
	       
	        // 返回匹配的检测项目
	        return maf.getTestMethod(standardNumber,testItem);
	    
	}

	@GetMapping("/check")
	public String excuteCheck(Model model,HttpServletRequest request) {
		HttpSession session = request.getSession();
		 String name=(String) session.getAttribute("username");
		return cds.result(model,name);
	}

	@GetMapping("/check/data")
	@ResponseBody
	public Map<String, Object> checkData(Model model,HttpServletRequest request) {
		HttpSession session = request.getSession();
		String name=(String) session.getAttribute("username");
		Map<String, Object> result = new LinkedHashMap<>();
		String view = cds.result(model,name);
		result.put("success", "checkDoneSimple".equals(view));
		result.put("view", view);
		result.put("presentList", model.asMap().get("presentList"));
		return result;
	}

	@GetMapping("/delete")
	@ResponseBody
	public String excutedelete() {

		int result = dss.delete();
		if (result == 0) {
			return "错误，没单子";
		} else {
			return "成功删除第" + result + "批样品";
		}

	}

	@GetMapping("/load")
	public void excuteload(HttpServletResponse response) {

		try {
			fdl.fileload(response);
		} catch (InvalidFormatException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
	@GetMapping("/loadBETA")
	public void excuteloadBETA(@RequestParam(value="allbatch", required=false) Integer batch,@RequestParam(value="dup", required=false)int dup,HttpServletResponse response) {

		try {
			fdl.fileloadBETA(response,batch,dup);
		} catch (InvalidFormatException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	@GetMapping("/loadF")
	public void excuteloadF(HttpServletResponse response) {

		try {
			fdl.fileFloadXML(response);
		} catch (InvalidFormatException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	@GetMapping("/loadSelomenLable")
	public void excuteloadSelomenLable(HttpServletResponse response,HttpServletRequest request,String table_name,String checkItem) {

		try {
			try {
				HttpSession session = request.getSession();
				String name=(String) session.getAttribute("username");
				fdl.fileSelomenLabalload(response, table_name,checkItem,name);
			} catch (SQLException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InvalidFormatException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	//@GetMapping("/loadRecordData")
	@RequestMapping("/loadRecordData")
	
	public void excuteloadRecordData(HttpServletResponse response,String table_name) throws IOException {

		try {
			
			fdl.recordDataload(table_name);
			String tableName = table_name.replace("/", "-");
			String fileName=tableName+".xlsx";
			File file=new File("./result/"+fileName);
			String filename=file.getName();
			
			FileInputStream fis= new FileInputStream(file);
			InputStream is=new BufferedInputStream(fis);
			byte[] buffer=new byte[is.available()];
			is.read(buffer);
			is.close();
			
			response.reset();
			response.setCharacterEncoding("UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(filename,"UTF-8"));
			response.addHeader("Content-Lenth", ""+file.length());
			OutputStream os=new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			os.write(buffer);
			os.flush();
			file.delete();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@GetMapping("/findSimple")
	public String excutefindSimple(Model model) {

		return ffs.result(model);
	}
	
}

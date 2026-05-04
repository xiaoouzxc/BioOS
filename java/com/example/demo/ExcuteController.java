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

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.service.CheckDoneSimple;
import com.test.service.DeleteSimpleSet;
import com.test.service.FileDownload;
import com.test.service.FindFSimple;
import com.test.service.MannualaddFsimple;
import com.test.service.StartTestingController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

	public String excuteTesting(Model model) {

		return stc.result(model);
	}

	@GetMapping("/manualAddFData")
	public String excuteManualAddFData(String simpleNum, String simpleName, String simpleItem, String testMethord) {

		return maf.manualAddF(simpleNum, simpleName, simpleItem, testMethord);
	}

	@GetMapping("/check")
	public String excuteCheck(Model model) {

		return cds.result(model);
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

	@GetMapping("/loadF")
	public void excuteloadF(HttpServletResponse response) {

		try {
			fdl.fileFload(response);
		} catch (InvalidFormatException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	@GetMapping("/loadSelomenLable")
	public void excuteloadSelomenLable(HttpServletResponse response,String table_name) {

		try {
			fdl.fileSelomenLabalload(response, table_name);
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

package com.gotogether.global.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gotogether.domain.order.entity.Order;
import com.gotogether.domain.ticketoptionanswer.entity.TicketOptionAnswer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExcelGenerator {

	private static final String SHEET_NAME = "구매참가자목록";
	private static final String[] HEADERS = {"이름", "이메일", "휴대폰 번호", "구매 일자", "티켓 이름"};

	public static byte[] generateParticipantExcel(List<Order> orders, Set<String> optionNames, 
			java.util.function.Function<Long, List<TicketOptionAnswer>> answerProvider) {
		try (Workbook workbook = new XSSFWorkbook();
			 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			
			Sheet sheet = workbook.createSheet(SHEET_NAME);
			
			createHeaderRow(sheet, optionNames);
			createDataRows(sheet, orders, optionNames, answerProvider);
			
			workbook.write(out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException("엑셀 파일 생성 중 오류 발생: ", e);
		}
	}

	private static void createHeaderRow(Sheet sheet, Set<String> optionNames) {
		Row headerRow = sheet.createRow(0);
		int colIdx = 0;
		
		for (String header : HEADERS) {
			headerRow.createCell(colIdx++).setCellValue(header);
		}
		
		for (String optionName : optionNames) {
			headerRow.createCell(colIdx++).setCellValue(optionName);
		}
	}

	private static void createDataRows(Sheet sheet, List<Order> orders, Set<String> optionNames,
			java.util.function.Function<Long, List<TicketOptionAnswer>> answerProvider) {
		int rowIdx = 1;
		
		for (Order order : orders) {
			Row row = sheet.createRow(rowIdx++);
			int cellIdx = 0;
			
			row.createCell(cellIdx++).setCellValue(order.getUser().getName());
			row.createCell(cellIdx++).setCellValue(order.getUser().getEmail());
			row.createCell(cellIdx++).setCellValue(order.getUser().getPhoneNumber());
			row.createCell(cellIdx++).setCellValue(order.getCreatedAt().toLocalDate().toString());
			row.createCell(cellIdx++).setCellValue(order.getTicket().getName());
			
			Map<String, String> optionAnswerMap = createOptionAnswerMap(order, answerProvider);
			for (String optionName : optionNames) {
				row.createCell(cellIdx++).setCellValue(optionAnswerMap.getOrDefault(optionName, ""));
			}
		}
	}

	private static Map<String, String> createOptionAnswerMap(Order order, 
			java.util.function.Function<Long, List<TicketOptionAnswer>> answerProvider) {
		Map<String, String> optionAnswerMap = new java.util.HashMap<>();
		
		List<TicketOptionAnswer> answers = answerProvider.apply(order.getId());
		for (TicketOptionAnswer answer : answers) {
			String optionName = answer.getTicketOption().getName();
			String value = getAnswerValue(answer);
			
			if (optionAnswerMap.containsKey(optionName)) {
				value = optionAnswerMap.get(optionName) + ", " + value;
			}
			optionAnswerMap.put(optionName, value);
		}
		
		return optionAnswerMap;
	}

	private static String getAnswerValue(TicketOptionAnswer answer) {
		if (answer.getAnswerText() != null) {
			return answer.getAnswerText();
		}
		if (answer.getTicketOptionChoice() != null) {
			return answer.getTicketOptionChoice().getName();
		}
		return "";
	}
} 
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;

public class FileManager
{
	// Category 클래스
	public static class Category
	{
		private String name;
		private Color color;
		
		public Category() {}
		public Category(String name, Color color)
		{
		    this.name = name;
		    this.color = color;
		}
		
		public String getName() { return name; }
		public Color getColor() { return color; }
		
		@Override
		public String toString() { return name; }
    }
	public static List<Category> categories;
	
	// Schedule 클래스
	public static class Schedule
	{
		private Category category; // 카테고리
		private String title, content; // 제목, 내용
		private LocalDateTime startDate, endDate; // 시작일, 종료일
		private boolean isDone; // 완료 여부
		
		public Category getCategory() { return category; }
		public String getTitle() { return title; }
		public String getContent() { return content; }
		public LocalDateTime getStartDate() { return startDate; }
		public LocalDateTime getEndDate() { return endDate; }
		public boolean isDone() { return isDone; }
		
		public Schedule() {}
		public Schedule(Category category, String title, String content, LocalDateTime startDate, LocalDateTime endDate, boolean isDone)
		{
			this.category = category;
			this.title = title;
			this.content = content;
			this.startDate = startDate;
			this.endDate = endDate;
			this.isDone = isDone;
		}
	}
	private static List<Schedule> schedules;
	
	// 파일 읽기 함수
	// 프로그램 시작 시에 1번만 호출
	private static void LoadSaveData()
	{
		categories = new ArrayList<>();
		schedules = new ArrayList<>();
		LoadCategory();
		LoadSchedule();
	}
	private static void LoadCategory()
	{
		try {
			// Schedule 읽기(data 파일)
			File data = new File(new File("").getAbsolutePath() + "/category.txt");
			if(!data.exists())
				return;
			
			FileInputStream in = new FileInputStream(data);
			
			byte[] header = new byte[8];
			while(in.read(header, 0, 8) == 8)
			{
				Category newCategory = new Category();
				
				// name 읽기
				int len = header[0] * 256 + header[1] + 128;
				byte[] datas = new byte[len];
				in.read(datas, 0, len);
				newCategory.name = new String(datas);
				
				int red = header[2] * 256 + header[3] + 128;
				int green = header[4] * 256 + header[5] + 128;
				int blue = header[6] * 256 + header[7] + 128;
				newCategory.color = new Color(red, green, blue);
				
				categories.add(newCategory);
			}
			in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	private static void LoadSchedule()
	{
		try {
			// Schedule 읽기(data 파일)
			File data = new File(new File("").getAbsolutePath() + "/data.txt");
			if(!data.exists())
				return;
			
			FileInputStream in = new FileInputStream(data);
			
			byte[] header = new byte[19];
			while(in.read(header, 0, 19) == 19)
			{
				Schedule newSchedule = new Schedule();
				
				// 각 헤더(2바이트)에 대해 반복
				for(int i = 0; i < 3; i++)
				{
					int len = header[i*2] * 256 + header[i*2+1] + 128;
					byte[] datas = new byte[len];
					in.read(datas, 0, len);
					
					switch(i)
					{
						case 0:
							String categoryName = new String(datas);
							for(Category category : categories)
							{
								if(category.name.equals(categoryName))
								{
									newSchedule.category = category;
									break;
								}
							}
							break;
						case 1:
							newSchedule.title = new String(datas);
							break;
						case 2:
							newSchedule.content = new String(datas);
							break;
					}
				}
				int year = header[6] * 256 + header[7] + 128;
				int month = header[8] + 128;
				int day = header[9] + 128;
				int hour = header[10] + 128;
				int minute = header[11] + 128;
				newSchedule.startDate = LocalDateTime.of(year, month, day, hour, minute);
				
				year = header[12] * 256 + header[13] + 128;
				month = header[14] + 128;
				day = header[15] + 128;
				hour = header[16] + 128;
				minute = header[17] + 128;
				newSchedule.endDate = LocalDateTime.of(year, month, day, hour, minute);
						
				newSchedule.isDone = header[18] == 1 ? true : false;
				
				schedules.add(newSchedule);
			}
			in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Category 저장 함수
	// Category 추가/삭제 시 자동 호출
	private static void saveCategory()
	{
		try {
			File data = new File(new File("").getAbsolutePath() + "/category.txt");
			if(!data.exists())
				data.createNewFile();
			
			FileOutputStream out = new FileOutputStream(data);
			
			byte[] bytes;
			for(Category category : categories)
			{
				byte[] header = new byte[8];
				
				// header
				header[0] = (byte)(category.name.getBytes().length / 256);
				header[1] = (byte)(category.name.getBytes().length % 256 - 128);
				header[2] = (byte)(category.color.getRed() / 256);
				header[3] = (byte)(category.color.getRed() % 256 - 128);
				header[4] = (byte)(category.color.getGreen() / 256);
				header[5] = (byte)(category.color.getGreen() % 256 - 128);
				header[6] = (byte)(category.color.getBlue() / 256);
				header[7] = (byte)(category.color.getBlue() % 256 - 128);
				out.write(header);
				
				// datas
				String str = String.format("%s", category.name);
				bytes = str.getBytes(StandardCharsets.UTF_8);
				out.write(bytes);
			}
			out.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	public static void addCategory(Category category)
	{
		categories.add(category);
		saveCategory();
	}
	// Schedule 삭제 함수
	public static void removeCategory(Category category)
	{
		categories.remove(category);
		saveSchedule();
	}
	
	// Schedule 저장 함수
	// Schedule 추가/삭제 시 자동 호출
	private static void saveSchedule()
	{
		try {
			File data = new File(new File("").getAbsolutePath() + "/data.txt");
			if(!data.exists())
				data.createNewFile();
			
			FileOutputStream out = new FileOutputStream(data);
			
			byte[] bytes;
			for(Schedule schedule : schedules)
			{
				byte[] header = new byte[19];
				
				// header
				header[0] = (byte)(schedule.category.name.getBytes().length / 256);
				header[1] = (byte)(schedule.category.name.getBytes().length % 256 - 128);
				header[2] = (byte)(schedule.title.getBytes().length / 256);
				header[3] = (byte)(schedule.title.getBytes().length % 256 - 128);
				header[4] = (byte)(schedule.content.getBytes().length / 256);
				header[5] = (byte)(schedule.content.getBytes().length % 256 - 128);
				header[6] = (byte)(schedule.startDate.getYear() / 256);
				header[7] = (byte)(schedule.startDate.getYear() % 256 - 128);
				header[8] = (byte)(schedule.startDate.getMonthValue() - 128);
				header[9] = (byte)(schedule.startDate.getDayOfMonth() - 128);
				header[10] = (byte)(schedule.startDate.getHour() - 128);
				header[11] = (byte)(schedule.startDate.getMinute() - 128);
				header[12] = (byte)(schedule.endDate.getYear() / 256);
				header[13] = (byte)(schedule.endDate.getYear() % 256 - 128);
				header[14] = (byte)(schedule.endDate.getMonthValue() - 128);
				header[15] = (byte)(schedule.endDate.getDayOfMonth() - 128);
				header[16] = (byte)(schedule.endDate.getHour() - 128);
				header[17] = (byte)(schedule.endDate.getMinute() - 128);
				header[18] = (byte)(schedule.isDone ? 1 : 0);
				out.write(header);
				
				// datas
				String str = String.format("%s", schedule.category.name);
				str += String.format("%s", schedule.title);
				str += String.format("%s", schedule.content);
				bytes = str.getBytes(StandardCharsets.UTF_8);
				out.write(bytes);
			}
			out.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	// Schedule 추가 함수
	public static void addSchedule(Schedule schedule)
	{
		schedules.add(schedule);
		saveSchedule();
	}
	// Schedule 삭제 함수
	public static void removeSchedule(Schedule schedule)
	{
		schedules.remove(schedule);
		saveSchedule();
	}
	
	// Schedule 검색 함수
    // 날짜로만 검색하기 위해 LocalDateTime이 아닌 LocalDate 사용
	// date ~ date + days 까지 검색
	// 오늘은 getSchedule(, 0)
	public static List<Schedule> getSchedule(LocalDate date, int days)
	{
		List<Schedule> searched = new ArrayList<>();
		for(Schedule schedule : schedules)
		{
			// endDate가 date(검색 시작일)보다 작거나 startDate가 date+days(검색 종료일)보다 크면 스킵
			if(schedule.endDate.toLocalDate().isBefore(date) || schedule.startDate.toLocalDate().isAfter(date.plusDays(days)))
				continue;
			
			searched.add(schedule);
		}
		return searched;
	}
	
	public static void main(String[] args) throws IOException
	{
		LoadSaveData();
		
//		addCategory(new Category("강의", Color.blue));
//		addSchedule(new Schedule(categories.get(0), "java", "발표\nppt", LocalDateTime.of(2024, 11, 19, 6, 30), LocalDateTime.of(2024, 11, 23, 12, 0), true));
		
//		for(Schedule schedule : getSchedule(LocalDate.of(2024, 11, 19), 0))
//			removeSchedule(schedule);
		
		// categories 전체 출력
//		for(Category category : categories)
//		{
//			System.out.println(String.format("name : %s", category.name));
//			System.out.println(String.format("color : %d-%d-%d", category.color.getRed(), category.color.getGreen(), category.color.getBlue()));
//		}
		
		// schedules 전체 출력
//		for(Schedule schedule : schedules)
//		{
//			System.out.println(String.format("category : %s", schedule.category.name));
//			System.out.println(String.format("color : %d-%d-%d", schedule.category.color.getRed(), schedule.category.color.getGreen(), schedule.category.color.getBlue()));
//			System.out.println(String.format("title : %s", schedule.title));
//			System.out.println(String.format("content : %s", schedule.content));
//			System.out.println(String.format("startDate : %s", schedule.startDate));
//			System.out.println(String.format("endDate : %s", schedule.endDate));
//			System.out.println(String.format("isDone : %b\n", schedule.isDone));
//		}
		
		// 검색 출력
		for(Schedule schedule : getSchedule(LocalDate.of(2024, 11, 19), 0))
		{
			System.out.println(String.format("category : %s", schedule.category.name));
			System.out.println(String.format("color : %d-%d-%d", schedule.category.color.getRed(), schedule.category.color.getGreen(), schedule.category.color.getBlue()));
			System.out.println(String.format("title : %s", schedule.title));
			System.out.println(String.format("content : %s", schedule.content));
			System.out.println(String.format("startDate : %s", schedule.startDate));
			System.out.println(String.format("endDate : %s", schedule.endDate));
			System.out.println(String.format("isDone : %b\n", schedule.isDone));
		}
	}
}
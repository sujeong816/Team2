package project1;

import java.awt.Color;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    // 카테고리와 일정 데이터를 저장하는 리스트
    public static List<Category> categories = new ArrayList<>();
    public static List<Schedule> schedules = new ArrayList<>();

    // Category 클래스
    public static class Category implements Serializable{
    	private static final long serialVersionUID = 1L; // 수정: 직렬화 버전 ID
        private String name;
        private Color color;

        public Category() {}

        public Category(String name, Color color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public Color getColor() {
            return color;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // Schedule 클래스
    public static class Schedule implements Serializable{
    	private static final long serialVersionUID = 1L; //수정: 직렬화 버전 ID
        private Category category;
        private String title, content;
        private LocalDateTime startDate, endDate;

        public Schedule() {}

        public Schedule(Category category, String title, String content, LocalDateTime startDate, LocalDateTime endDate) {
            this.category = category;
            this.title = title;
            this.content = content;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public Category getCategory() {
            return category;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public LocalDateTime getStartDate() {
            return startDate;
        }

        public LocalDateTime getEndDate() {
            return endDate;
        }
    }

    // 데이터 로드 함수
    public static void LoadSaveData() {
        // 카테고리와 일정 리스트가 null인 경우 초기화
        if (categories == null) categories = new ArrayList<>(); // 수정: categories 초기화 추가
        if (schedules == null) schedules = new ArrayList<>();   // 수정: schedules 초기화 추가
        LoadCategory();
        LoadSchedule();
    }

    // 모든 데이터를 저장하는 함수
    public static void SaveAllData() {
        saveCategory();
        saveSchedule();
    }

    // 카테고리 로드 함수
    private static void LoadCategory() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("category.txt"))) {
            categories = (List<Category>) in.readObject(); // 수정: ObjectInputStream을 사용하여 데이터를 읽어옴
        } catch (Exception e) {
            categories = new ArrayList<>(); // 수정: 실패 시 빈 리스트로 초기화
        }
    }

    // 일정 로드 함수
    private static void LoadSchedule() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("data.txt"))) {
            schedules = (List<Schedule>) in.readObject(); // 수정: ObjectInputStream을 사용하여 데이터를 읽어옴
        } catch (Exception e) {
            schedules = new ArrayList<>(); // 수정: 실패 시 빈 리스트로 초기화
        }
    }

    // 카테고리 저장 함수
    private static void saveCategory() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("category.txt"))) {
            out.writeObject(categories); // 수정: ObjectOutputStream을 사용하여 데이터를 저장
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 일정 저장 함수
    private static void saveSchedule() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("data.txt"))) {
            out.writeObject(schedules); // 수정: ObjectOutputStream을 사용하여 데이터를 저장
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 카테고리 추가 함수
    public static void addCategory(Category category) {
        // 수정: 중복된 카테고리 방지
        boolean exists = categories.stream()
            .anyMatch(existing -> existing.getName().equals(category.getName()));
        if (!exists) {
            categories.add(category);
            saveCategory(); // 수정: 추가된 카테고리를 저장
        }
    }

    // 일정 추가 함수
    public static void addSchedule(Schedule schedule) {
        schedules.add(schedule);
        saveSchedule(); // 수정: 추가된 일정을 저장
    }
    
    //수정: 초기화 함수
    public static void resetData() {
        // 리스트 초기화
        categories.clear();
        schedules.clear();

        // 저장된 파일 삭제 또는 빈 데이터로 저장
        saveCategory(); // 빈 카테고리 저장
        saveSchedule(); // 빈 일정 저장

        System.out.println("모든 데이터가 초기화되었습니다.");
    }

}

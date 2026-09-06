import java.text.Collator
import java.text.Normalizer
import java.util.Locale
// 1. LỚP STUDENT
data class Student(
    val studentId: String,
    val fullName: String,
    val age: Int,
    val major: String,
    val gpa: Double
)
// ============================================================
// 2. HÀM BỎ DẤU TIẾNG VIỆT
// ============================================================
fun removeVietnameseAccents(text: String): String {
    var result = Normalizer.normalize(text, Normalizer.Form.NFD)
    result = result.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    result = result.replace("đ", "d").replace("Đ", "D")
    return result
}
// ============================================================
// 3. CHUẨN HÓA CHUỖI ĐỂ TÌM KIẾM
// ============================================================
fun normalizeText(text: String): String {
    return removeVietnameseAccents(text).lowercase().trim()
}

// ============================================================
// 4. LẤY TÊN CỦA SINH VIÊN
// ============================================================
fun getStudentName(fullName: String): String {
    return fullName.trim().substringAfterLast(" ")
}

// ============================================================
// 5. BỘ SO SÁNH TIẾNG VIỆT
// ============================================================
fun vietnameseCollator(): Collator {
    val collator = Collator.getInstance(Locale.forLanguageTag("vi-VN"))
    // PRIMARY: Ưu tiên so sánh chữ cái theo ngôn ngữ.
    collator.strength = Collator.PRIMARY
    return collator
}

// ============================================================
// 6. SẮP XẾP THEO TÊN TIẾNG VIỆT
// ============================================================
fun sortByVietnameseName(students: List<Student>): List<Student> {
    val collator = vietnameseCollator()
    return students.sortedWith { student1, student2 ->
        val name1 = getStudentName(student1.fullName)
        val name2 = getStudentName(student2.fullName)
        
        val compareName = collator.compare(name1, name2) // So sánh tên trước
        if (compareName != 0) {
            compareName
        } else {
            // Nếu trùng tên: so sánh toàn bộ họ tên.
            collator.compare(student1.fullName, student2.fullName)
        }
    }
}

// ============================================================
// 7. IN MỘT SINH VIÊN
// ============================================================
fun displayStudent(student: Student) {
    println("----------------------------------------")
    println("Student ID : ${student.studentId}")
    println("Full Name  : ${student.fullName}")
    println("Age        : ${student.age}")
    println("Major      : ${student.major}")
    println("GPA        : ${"%.2f".format(student.gpa)}")
}

// ============================================================
// 8. IN DANH SÁCH SINH VIÊN
// ============================================================
fun displayStudentList(students: List<Student>) {
    if (students.isEmpty()) {
        println("Không có sinh viên.")
        return
    }
    for ((index, student) in students.withIndex()) {
        println("\nStudent ${index + 1}")
        displayStudent(student)
    }
    println("----------------------------------------")
    println("Total students: ${students.size}")
}

// ============================================================
// 9. KIỂM TRA ID ĐÃ TỒN TẠI
// ============================================================
fun idExists(students: List<Student>, id: String): Boolean {
    return students.any { it.studentId.equals(id, ignoreCase = true) }
}

// ============================================================
// 10. ADD STUDENT
// ============================================================
fun addStudent(students: MutableList<Student>) {
    println("\n========== ADD STUDENT ==========")
    print("Student ID: ")
    val id = readln().trim()
    if (id.isEmpty()) {
        println("Student ID không được để trống.")
        return
    }
    if (idExists(students, id)) {
        println("Student ID đã tồn tại.")
        return
    }

    print("Full Name: ")
    val fullName = readln().trim()
    if (fullName.isEmpty()) {
        println("Tên không được để trống.")
        return
    }

    print("Age: ")
    val age = readln().trim().toIntOrNull()
    if (age == null || age <= 0) {
        println("Tuổi không hợp lệ.")
        return
    }

    print("Major: ")
    val major = readln().trim()
    if (major.isEmpty()) {
        println("Ngành không được để trống.")
        return
    }

    print("GPA (0 - 10): ")
    val gpa = readln().trim().toDoubleOrNull()
    if (gpa == null || gpa < 0.0 || gpa > 10.0) {
        println("GPA phải từ 0 đến 10.")
        return
    }

    val student = Student(id, fullName, age, major, gpa)
    students.add(student)
    println("\nThêm sinh viên thành công.")
    displayStudent(student)
}

// ============================================================
// 11. DISPLAY ALL STUDENTS
// ============================================================
fun displayAllStudents(students: List<Student>) {
    println("\n========== STUDENT LIST ==========")
    displayStudentList(students)
}

// ============================================================
// 12. SEARCH STUDENT BY ID
// ============================================================
fun searchStudentById(students: List<Student>) {
    print("Enter Student ID: ")
    val id = readln().trim()
    val student = students.find { it.studentId.equals(id, ignoreCase = true) }

    if (student != null) {
        println("\nTìm thấy sinh viên:")
        displayStudent(student)
    } else {
        println("Không tìm thấy sinh viên.")
    }
}

// ============================================================
// 13. CALCULATE AVERAGE GPA
// ============================================================
fun calculateAverageGpa(students: List<Student>) {
    if (students.isEmpty()) {
        println("Danh sách sinh viên trống.")
        return
    }
    var total = 0.0
    for (student in students) {
        total += student.gpa
    }
    val average = total / students.size
    println("Average GPA = %.2f".format(average))
}

// ============================================================
// YÊU CẦU 1: ĐẾM SINH VIÊN GPA >= 8.0
// ============================================================
fun countGpaAbove8(students: List<Student>) {
    val count = students.count { it.gpa >= 8.0 }
    println("Số sinh viên có GPA >= 8.0: $count")
}

// ============================================================
// YÊU CẦU 2: ĐẾM SINH VIÊN GPA < 5.0
// ============================================================
fun countGpaBelow5(students: List<Student>) {
    val count = students.count { it.gpa < 5.0 }
    println("Số sinh viên có GPA < 5.0: $count")
}

// ============================================================
// YÊU CẦU 3: GPA TRUNG BÌNH THEO NGÀNH
// ============================================================
fun averageGpaByMajor(students: List<Student>) {
    print("Nhập ngành: ")
    val majorInput = readln().trim()
    val normalizedMajor = normalizeText(majorInput)
    val result = students.filter { normalizeText(it.major) == normalizedMajor }

    if (result.isEmpty()) {
        println("Không tìm thấy sinh viên thuộc ngành này.")
        return
    }

    var total = 0.0
    for (student in result) {
        total += student.gpa
    }
    val average = total / result.size

    println("\nMajor: $majorInput")
    println("Number of students: ${result.size}")
    println("Average GPA: %.2f".format(average))
}

// ============================================================
// YÊU CẦU 4: TÌM SINH VIÊN GPA CAO NHẤT
// ============================================================
fun findHighestGpa(students: List<Student>) {
    if (students.isEmpty()) {
        println("Danh sách trống.")
        return
    }
    val highestGpa = students.maxOf { it.gpa }
    val result = students.filter { it.gpa == highestGpa }

    println("\n========== HIGHEST GPA ==========")
    displayStudentList(result)
}

// ============================================================
// YÊU CẦU 5: TÌM SINH VIÊN LỚN TUỔI NHẤT
// ============================================================
fun findOldestStudent(students: List<Student>) {
    if (students.isEmpty()) {
        println("Danh sách trống.")
        return
    }
    val maxAge = students.maxOf { it.age }
    val result = students.filter { it.age == maxAge }

    println("\n========== OLDEST STUDENT ==========")
    displayStudentList(result)
}

// ============================================================
// YÊU CẦU 6: TÌM GPA TRONG KHOẢNG 7.0 -> 8.5
// ============================================================
fun findGpaFrom7To85(students: List<Student>) {
    val result = students.filter { it.gpa in 7.0..8.5 }
    println("\n========== GPA 7.0 -> 8.5 ==========")
    if (result.isEmpty()) {
        println("Không có sinh viên GPA từ 7.0 đến 8.5.")
        return
    }
    displayStudentList(result)
}

// ============================================================
// YÊU CẦU 7: TÌM TẤT CẢ SINH VIÊN THUỘC MỘT NGÀNH
// ============================================================
fun findStudentsByMajor(students: List<Student>) {
    print("Nhập ngành cần tìm: ")
    val majorInput = readln().trim()
    val searchMajor = normalizeText(majorInput)
    val result = students.filter { normalizeText(it.major) == searchMajor }

    println("\n========== SEARCH BY MAJOR ==========")
    if (result.isEmpty()) {
        println("Không tìm thấy sinh viên thuộc ngành $majorInput.")
        return
    }
    displayStudentList(result)
}

// ============================================================
// YÊU CẦU 8: TÌM SINH VIÊN THEO MỘT PHẦN TÊN
// ============================================================
fun searchStudentByPartialName(students: List<Student>) {
    print("Nhập một phần tên: ")
    val keyword = normalizeText(readln())
    val result = students.filter { normalizeText(it.fullName).contains(keyword) }

    println("\n========== SEARCH RESULT ==========")
    if (result.isEmpty()) {
        println("Không tìm thấy sinh viên.")
        return
    }
    displayStudentList(result)
}

// ============================================================
// YÊU CẦU 9: SẮP XẾP GPA GIẢM DẦN
// ============================================================
fun sortByGpaDescending(students: List<Student>) {
    val result = students.sortedByDescending { it.gpa }
    println("\n========== GPA DESCENDING ==========")
    displayStudentList(result)
}

// ============================================================
// YÊU CẦU 10: HIỂN THỊ TOP 3 GPA CAO NHẤT
// ============================================================
fun displayTop3Gpa(students: List<Student>) {
    if (students.isEmpty()) {
        println("Danh sách trống.")
        return
    }
    val top3 = students.sortedByDescending { it.gpa }.take(3)
    println("\n========== TOP 3 GPA ==========")
    displayStudentList(top3)
}

// ============================================================
// YÊU CẦU 11: SẮP XẾP THEO TUỔI (nhỏ tuổi -> lớn tuổi)
// ============================================================
fun sortByAge(students: List<Student>) {
    val result = students.sortedBy { it.age }
    println("\n========== SORT BY AGE ==========")
    displayStudentList(result)
}

// ============================================================
// YÊU CẦU 12: SẮP XẾP THEO TÊN TIẾNG VIỆT
// ============================================================
fun displaySortByVietnameseName(students: List<Student>) {
    val result = sortByVietnameseName(students)
    println("\n========== SORT BY VIETNAMESE NAME ==========")
    displayStudentList(result)
}

// ============================================================
// 14. REMOVE STUDENT
// ============================================================
fun removeStudent(students: MutableList<Student>) {
    print("Nhập Student ID cần xóa: ")
    val id = readln().trim()
    val student = students.find { it.studentId.equals(id, ignoreCase = true) }

    if (student == null) {
        println("Không tìm thấy sinh viên.")
        return
    }
    students.remove(student)
    println("\nĐã xóa sinh viên:")
    displayStudent(student)
}

// ============================================================
// 15. MENU
// ============================================================
fun displayMenu() {
    println("\n==============================================")
    println("          STUDENT MANAGEMENT")
    println("==============================================")
    println("1.  Add student")
    println("2.  Display all students")
    println("3.  Search student by ID")
    println("4.  Calculate average GPA")
    println("5.  Find student with highest GPA")
    println("6.  Remove student")
    println("----------------------------------------------")
    println("7.  Count students GPA >= 8.0")
    println("8.  Count students GPA < 5.0")
    println("9.  Average GPA by major")
    println("10. Find oldest student")
    println("11. Find GPA from 7.0 to 8.5")
    println("12. Find students by major")
    println("13. Search student by part of name")
    println("14. Sort GPA descending")
    println("15. Display top 3 GPA")
    println("16. Sort by age")
    println("17. Sort by Vietnamese name")
    println("0.  Exit")
    println("==============================================")
    print("Choose: ")
}

// ============================================================
// 16. MAIN
// ============================================================
fun main() {
    val students = mutableListOf(
        Student("SV001", "Nguyễn Văn Ánh", 20, "Công nghệ thông tin", 8.70),
        Student("SV002", "Trần Minh Bình", 22, "Công nghệ thông tin", 7.40),
        Student("SV003", "Lê Hoàng Ân", 21, "Điện tử vi mạch", 9.20),
        Student("SV004", "Phạm Đức Đạt", 23, "Tự động hóa", 4.80),
        Student("SV005", "Võ Thị Hằng", 20, "Điện tử vi mạch", 8.10)
    )

    var choice: Int
    do {
        displayMenu()
        choice = readln().trim().toIntOrNull() ?: -1

        when (choice) {
            1 -> addStudent(students)
            2 -> displayAllStudents(students)
            3 -> searchStudentById(students)
            4 -> calculateAverageGpa(students)
            5 -> findHighestGpa(students)
            6 -> removeStudent(students)
            7 -> countGpaAbove8(students)
            8 -> countGpaBelow5(students)
            9 -> averageGpaByMajor(students)
            10 -> findOldestStudent(students)
            11 -> findGpaFrom7To85(students)
            12 -> findStudentsByMajor(students)
            13 -> searchStudentByPartialName(students)
            14 -> sortByGpaDescending(students)
            15 -> displayTop3Gpa(students)
            16 -> sortByAge(students)
            17 -> displaySortByVietnameseName(students)
            0 -> println("\nProgram terminated.")
            else -> println("\nLựa chọn không hợp lệ.")
        }
    } while (choice != 0)
}
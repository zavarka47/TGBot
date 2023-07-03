package com.example.tgbot.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table (name = "task")
public class Task implements Comparable<Task> {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "task_id")
    private Long id;
    @Column (name = "chat_id")
    private Long chatId;
    @Column (name = "date_time")
    private LocalDateTime dateTime;
    private String text;

    @Override
    public String toString() {
        return "[" + dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + "] " + text;
    }

    @Override
    public int compareTo(@NotNull Task o) {
        if (this.getDateTime().isAfter(o.getDateTime())){
            return 1;
        } else if (this.getDateTime().isBefore(o.getDateTime())){
            return -1;
        } else {
            return 0;
        }
    }



}

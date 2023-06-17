package com.example.tgbot.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table (name = "task")
public class Task {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "task_id")
    private Long id;
    @Column (name = "chat_id")
    private Long chatId;
    @Column (name = "date_time")
    private LocalDateTime dateTime;
    private String text;

}

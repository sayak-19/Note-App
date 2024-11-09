package com.example.note_app.service;

import com.example.note_app.entity.AuditLog;
import com.example.note_app.entity.Note;
import com.example.note_app.repository.AuditLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuditLogService {

    private final AuditLogRepository repo;

    public void auditNoteAction(String action, Note note) {
        repo.save(AuditLog.builder()
                .action(action)
                .username(note.getOwnerUsername())
                .noteId(note.getId())
                .time(note.getCreatedAt()).build());
    }

    public List<AuditLog> fetchAllLog() {
        return repo.findAll();
    }
}

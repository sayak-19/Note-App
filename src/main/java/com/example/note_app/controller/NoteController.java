package com.example.note_app.controller;

import com.example.note_app.entity.Note;
import com.example.note_app.service.NoteService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService service;

    @GetMapping
    public List<Note> getNotesForUser(@AuthenticationPrincipal UserDetails userdetails){
        return service.getNotesForUser(userdetails.getUsername());
    }

    @PostMapping
    public Note createNoteForUser(@RequestBody String content, @AuthenticationPrincipal UserDetails userDetails){
        return service.createNoteForUser(content, userDetails.getUsername());
    }

    @PutMapping
    public Note updateNote(@RequestBody String content, @PathVariable Long noteId){
        return service.updateNoteForUser(noteId, content);
    }

    @DeleteMapping
    public void deleteNote(@PathVariable Long noteId){
        service.deleteNoteForUser(noteId);
    }
}

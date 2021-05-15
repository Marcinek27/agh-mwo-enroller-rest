package com.company.enroller.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    // GET http://localhost:8080/meetings
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    // GET http://localhost:8080/meetings/2
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    // POST http://localhost:8080/meetings/3
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> registerMeeting(@RequestBody Meeting meeting) {
        if (meetingService.findById(meeting.getId()) != null) {
            return new ResponseEntity("Unable to create. A meeting with id " + meeting.getId() + " already exist.",
                    HttpStatus.CONFLICT);
        }
        meetingService.add(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
    }

    // POST http://localhost:8080/meetings/2/participants
    // znalezc spotkanie
    // znalezc uczestnika w systemie
    // meeting.addParticipant(participant);
    // update meetingService
    // odeslij status 200 OK (+dane)

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
    public ResponseEntity<?> registerParticipantForMeeting(@PathVariable("id") long id,
            @RequestBody Participant participant) {
        Meeting foundMeeting = meetingService.findById(id);
        Participant foundParticipant = participantService.findByLogin(participant.getLogin());
        if (foundMeeting == null) {
            return new ResponseEntity("Meeting not found.", HttpStatus.NOT_FOUND);
        }
        if (foundParticipant == null) {
            return new ResponseEntity("Unable to add participant. A participant with login " + participant.getLogin()
                    + " does not exist.", HttpStatus.NOT_FOUND);
        }
        foundMeeting.addParticipant(foundParticipant);
        meetingService.update(foundMeeting);
        return new ResponseEntity<Participant>(foundParticipant, HttpStatus.OK);
    }

    // GET http://localhost:8080/meetings/2/participants
    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long id) {
        Meeting foundMeeting = meetingService.findById(id);
        if (foundMeeting == null) {
            return new ResponseEntity("Meeting not found.", HttpStatus.NOT_FOUND);
        }
        Collection<Participant> participants = foundMeeting.getParticipants();
        return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
    }

    // DELETE http://localhost:8080/meetings/4
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meetingService.delete(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    // PUT http://localhost:8080/meetings/4
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting meeting) {
        Meeting foundMeeting = meetingService.findById(id);
        if (foundMeeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        foundMeeting.setTitle(meeting.getTitle());
        foundMeeting.setDescription(meeting.getDescription());
        foundMeeting.setDate(meeting.getDate());
        meetingService.update(foundMeeting);
        return new ResponseEntity<Meeting>(foundMeeting, HttpStatus.OK);
    }
    
    // delete participant from meeting
    // DEL http://localhost:8080/meetings/5/participants
    @RequestMapping(value = "/{id}/participants", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteParticipantForMeeting(@PathVariable("id") long id,
            @RequestBody Participant participant) {
        Meeting foundMeeting = meetingService.findById(id);
        Participant foundParticipant = participantService.findByLogin(participant.getLogin());
        if (foundMeeting == null) {
            return new ResponseEntity("Meeting not found.", HttpStatus.NOT_FOUND);
        }
        if (foundParticipant == null) {
            return new ResponseEntity("Unable to remove participant. A participant with login " + participant.getLogin()
                    + " does not exist.", HttpStatus.NOT_FOUND);
        }
        foundMeeting.removeParticipant(foundParticipant);
        meetingService.update(foundMeeting);
        return new ResponseEntity<Participant>(foundParticipant, HttpStatus.OK);
    }
}

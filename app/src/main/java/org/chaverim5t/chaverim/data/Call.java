package org.chaverim5t.chaverim.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yakov on 8/17/15.
 */
public class Call {
  public String title;
  public int callNumber;
  public long callId;
  public Date createdTime;
  public Date updatedTime;
  public String disptacherName;
  public String callerName;
  public String callerNumber;
  public String notes;
  public String vehicle;
  public String problem;
  public String region;
  public String status;
  public boolean urgent;
  public String location;

  public List<VoiceNote> voiceNotes;

  public List<Message> messages;

  public List<String> coverage;


  class Message {
    public Date date;
    public String message;

    public Message() {}
  }

  public Message newMessage() {
    return new Message();
  }

  public Call() {
    title = "";
    callNumber = 0;
    callerName = "";
    callerNumber = "";
    callId = 0;
    createdTime = new Date();
    updatedTime = new Date();
    disptacherName = "";
    notes = "";
    vehicle = "";
    location = "";
    problem = "";
    region = "F";
    status = "Open";
    urgent = false;
    voiceNotes = new ArrayList<>();
    messages = new ArrayList<>();
    coverage = new ArrayList<>();
  }

  public Call(String title) {
    this();
    this.title = title;
  }
}

package no.kristiania.http.controller;

import no.kristiania.db.Member;
import no.kristiania.db.MemberDao;
import no.kristiania.db.Task;
import no.kristiania.db.TaskMemberDao;
import no.kristiania.http.HttpMessage;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedHashSet;

public class TaskFilterGetController extends AbstractController {
    private final MemberDao memberDao;
    private final TaskMemberDao taskMemberDao;

    public TaskFilterGetController(DataSource dataSource) {
        this.memberDao = new MemberDao(dataSource);
        this.taskMemberDao = new TaskMemberDao(dataSource);
    }

    @Override
    public void handle(HttpMessage request, Socket socket) throws IOException, SQLException {
        StringBuilder body = new StringBuilder();

        body.append("<ul>");
        if(TaskFilterPostController.getFilterList() != null){
            for(Task task : TaskFilterPostController.getFilterList()){

                body.append("<li id =\"task-li-").append(task.getId()).append("\"><strong>Task: </strong> ")
                        .append(task.getName()).append(" <strong>Description: </strong>").append(task.getDescription())
                        .append("  <strong>Status: </strong>").append(task.getStatus().toString())
                        .append("</li>");

                LinkedHashSet <Long> memberIDsOnTask = taskMemberDao.retrieveMembersByTaskId(task.getId());

                if(memberIDsOnTask.size() > 0){
                    body.append("<ul>");

                    for(Long memberId : memberIDsOnTask){
                        Member member = memberDao.retrieve(memberId);
                        body.append("<li>").append(member.getFirstName()).append(" ").append(member.getLastName()).append("</li>");
                    }
                    body.append("</ul>");
                }
            }

            body.append("</ul>");
        }
        sendGetResponse(socket, body);
    }

}
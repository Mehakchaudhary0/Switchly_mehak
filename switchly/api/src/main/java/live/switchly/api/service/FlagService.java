package live.switchly.api.service;

import live.switchly.api.exception.ConflictException;
import live.switchly.api.exception.NotFoundException;
import live.switchly.api.model.Flag;
import live.switchly.api.model.Project;
import live.switchly.api.repository.FlagRepository;
import live.switchly.api.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FlagService {

    private final FlagRepository flagRepository;
    private final ProjectRepository projectRepository;

    public FlagService(
            FlagRepository flagRepository,
            ProjectRepository projectRepository) {

        this.flagRepository = flagRepository;
        this.projectRepository = projectRepository;
    }

    public Flag create(
            UUID projectId,
            String key,
            String name,
            String description) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new NotFoundException("Project " + projectId + " not found"));

        if (flagRepository.existsByProjectIdAndKey(projectId, key)) {
            throw new ConflictException(
                    "Flag with key " + key + " already exists in project " + projectId);
        }

        Flag flag = new Flag(
                UUID.randomUUID(),
                project.getOrganizationId(),
                projectId,
                key,
                name,
                description,
                false
        );

        return flagRepository.save(flag);
    }

    public List<Flag> getAllForProject(UUID projectId) {
        projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new NotFoundException("Project " + projectId + " not found"));

        return flagRepository.findByProjectId(projectId);
    }

    public Flag getById(UUID flagId) {
        return flagRepository.findById(flagId)
                .orElseThrow(() ->
                        new NotFoundException("Flag " + flagId + " not found"));
    }

    public Flag setEnabled(UUID flagId, boolean enabled) {
        Flag flag = getById(flagId);

        flag.setEnabled(enabled);

        return flagRepository.save(flag);
    }

    public void delete(UUID flagId) {
        Flag flag = getById(flagId);

        flagRepository.deleteById(flag.getId());
    }
}
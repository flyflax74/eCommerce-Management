package com.ecommerce.site.admin.service;

import java.util.List;
import java.util.NoSuchElementException;

import com.ecommerce.site.admin.exception.SectionNotFoundException;
import com.ecommerce.site.admin.exception.SectionUnmoveableException;
import com.ecommerce.site.admin.model.entity.Section;
import com.ecommerce.site.admin.repository.SectionRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional(rollbackFor = Exception.class)
public class SectionService {

	@Autowired
	private SectionRepository repository;
	
	public List<Section> listSections() {
		return repository.findAllSectionsSortedByOrder();
	}
	
	public void saveSection(@NotNull Section section) {
		if (section.getId() == null) {
			setPositionForNewSection(section);
		}
		repository.save(section);
	}	
	
	private void setPositionForNewSection(@NotNull Section section) {
		long newPosition = repository.count() + 1;
		section.setSectionOrder((int) newPosition);
	}
	
	public Section getSection(Integer id) throws SectionNotFoundException {
		try {
			return repository.findById(id).orElseThrow();
		} catch (NoSuchElementException ex) {
			throw new SectionNotFoundException("Could not find any section with ID " + id);
		}
	}	
	
	public void deleteSection(Integer id) throws SectionNotFoundException {
		if (!repository.existsById(id)) {
			throw new SectionNotFoundException("Could not find any section with ID " + id);
		}
		
		repository.deleteById(id);
	}
	
	public void updateSectionEnabledStatus(Integer id, boolean enabled) 
			throws SectionNotFoundException {
		if (!repository.existsById(id)) {
			throw new SectionNotFoundException("Could not find any section with ID " + id);
		}
		
		repository.updateEnabledStatus(id, enabled);
	}

	public void moveSectionUp(Integer id) throws SectionNotFoundException, SectionUnmoveableException {
		Section currentSection = repository.getSimpleSectionById(id);
		if (currentSection == null) {
			throw new SectionNotFoundException("Could not find any section with ID " + id);
		}
		
		List<Section> allSections = repository.getOnlySectionIDsSortedByOrder();
		
		int currentSectionIndex = allSections.indexOf(currentSection);
		if (currentSectionIndex == 0) {
			throw new SectionUnmoveableException("The section ID " + id + " is already in the first position");
		}
		
		int previousSectionIndex = currentSectionIndex - 1;
		Section previousSection = allSections.get(previousSectionIndex);
		
		currentSection.setSectionOrder(previousSectionIndex + 1);		
		previousSection.setSectionOrder(currentSectionIndex + 1);
		
		repository.updateSectionPosition(currentSection.getSectionOrder(), currentSection.getId());
		repository.updateSectionPosition(previousSection.getSectionOrder(), previousSection.getId());
	}
	
	public void moveSectionDown(Integer id) throws SectionNotFoundException, SectionUnmoveableException {
		Section currentSection = repository.getSimpleSectionById(id);
		if (currentSection == null) {
			throw new SectionNotFoundException("Could not find any section with ID " + id);
		}
		
		List<Section> allSections = repository.getOnlySectionIDsSortedByOrder();
		
		int currentSectionIndex = allSections.indexOf(currentSection);
		if (currentSectionIndex == allSections.size() - 1) {
			throw new SectionUnmoveableException("The section ID " + id + " is already in the last position");
		}
		
		int nextSectionIndex = currentSectionIndex + 1;
		Section nextSection = allSections.get(nextSectionIndex);
		
		currentSection.setSectionOrder(nextSectionIndex + 1);		
		nextSection.setSectionOrder(currentSectionIndex + 1);
		
		repository.updateSectionPosition(currentSection.getSectionOrder(), currentSection.getId());
		repository.updateSectionPosition(nextSection.getSectionOrder(), nextSection.getId());
	}	
}

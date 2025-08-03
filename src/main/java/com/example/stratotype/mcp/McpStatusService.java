package com.example.stratotype.mcp;

import static java.beans.Introspector.getBeanInfo;
import static org.springframework.beans.BeanUtils.copyProperties;
import com.example.stratotype.aop.CallLogging;
import com.example.stratotype.mcp.data.*;
import com.example.stratotype.mcp.data.RecentEventsDto.EventDto;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
@CallLogging
public class McpStatusService {

  private final Map<String, BasicStatusDto> basicStatusMap = new HashMap<>();
  private final Map<String, ResourceStatusDto> resourceStatusMap = new HashMap<>();
  private final Map<String, MilitaryStatusDto> militaryStatusMap = new HashMap<>();
  private final Map<String, DiplomaticRelationsDto> diplomaticRelationsMap = new HashMap<>();
  private final Map<String, InternalStabilityDto> internalStabilityMap = new HashMap<>();
  private final Map<String, RecentEventsDto> recentEventsMap = new HashMap<>();
  private final Map<String, PoliticalStatusDto> politicalStatusMap = new HashMap<>();

  public BasicStatusDto getBasicStatus(String nationName) {
    return basicStatusMap.get(nationName);
  }

  public ResourceStatusDto getResourceStatus(String nationName) {
    return resourceStatusMap.get(nationName);
  }

  public MilitaryStatusDto getMilitaryStatus(String nationName) {
    return militaryStatusMap.get(nationName);
  }

  public DiplomaticRelationsDto getDiplomaticRelations(String nationName) {
    return diplomaticRelationsMap.get(nationName);
  }

  public PoliticalStatusDto getPoliticalStatus(String nationName) {
    return politicalStatusMap.get(nationName);
  }

  public RecentEventsDto getRecentEvents(String nationName) {
    return recentEventsMap.get(nationName);
  }

  public InternalStabilityDto getInternalStability(String nationName) {
    return internalStabilityMap.get(nationName);
  }

  public void updateBasicStatus(String nationName, BasicStatusDto status) {
    BasicStatusDto existing = basicStatusMap.get(nationName);
    if (existing == null) {
      basicStatusMap.put(nationName, status);
    } else {
      copyProperties(status, existing, getNullPropertyNames(status));
    }
  }

  public void updateResourceStatus(String nationName, ResourceStatusDto status) {
    ResourceStatusDto existing = resourceStatusMap.get(nationName);
    if (existing == null) {
      resourceStatusMap.put(nationName, status);
    } else {
      copyProperties(status, existing, getNullPropertyNames(status));
    }
  }

  public void updateMilitaryStatus(String nationName, MilitaryStatusDto status) {
    MilitaryStatusDto existing = militaryStatusMap.get(nationName);
    if (existing == null) {
      militaryStatusMap.put(nationName, status);
    } else {
      copyProperties(status, existing, getNullPropertyNames(status));
    }
  }

  public void updateDiplomaticRelations(String nationName, DiplomaticRelationsDto status) {
    DiplomaticRelationsDto existing = diplomaticRelationsMap.get(nationName);
    if (existing == null) {
      diplomaticRelationsMap.put(nationName, status);
    } else {
      copyProperties(status, existing, getNullPropertyNames(status));
    }
  }

  public void updatePoliticalStatus(String nationName, PoliticalStatusDto status) {
    PoliticalStatusDto existing = politicalStatusMap.get(nationName);
    if (existing == null) {
      politicalStatusMap.put(nationName, status);
    } else {
      copyProperties(status, existing, getNullPropertyNames(status));
    }
  }

  public void addRecentEvent(String nationName, EventDto eventDto) {
    RecentEventsDto existing = recentEventsMap.get(nationName);
    if (existing == null) {
      existing = new RecentEventsDto();
      existing.setNationName(nationName);
      existing.setEvents(new java.util.ArrayList<>());
      recentEventsMap.put(nationName, existing);
    }
    existing.getEvents().add(eventDto);
  }


  public void updateInternalStability(String nationName, InternalStabilityDto status) {
    InternalStabilityDto existing = internalStabilityMap.get(nationName);
    if (existing == null) {
      internalStabilityMap.put(nationName, status);
    } else {
      copyProperties(status, existing, getNullPropertyNames(status));
    }
  }

  private static String[] getNullPropertyNames(Object source) {
    final java.beans.BeanInfo beanInfo;
    try {
      beanInfo = getBeanInfo(source.getClass(), Object.class);
    } catch (java.beans.IntrospectionException e) {
      throw new RuntimeException(e);
    }
    return java.util.Arrays.stream(beanInfo.getPropertyDescriptors())
      .map(java.beans.PropertyDescriptor::getName)
      .filter(name -> {
        try {
          return java.util.Arrays.stream(beanInfo.getPropertyDescriptors())
            .anyMatch(pd -> {
              try {
                return pd.getReadMethod().invoke(source) == null;
              } catch (Exception e) {
                return false;
              }
            });
        } catch (Exception e) {
          return false;
        }
      })
      .toArray(String[]::new);
  }
}
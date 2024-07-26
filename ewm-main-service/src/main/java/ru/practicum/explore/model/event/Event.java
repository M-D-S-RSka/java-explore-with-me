package ru.practicum.explore.model.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import ru.practicum.explore.model.category.Category;
import ru.practicum.explore.model.compilation.Compilation;
import ru.practicum.explore.model.user.User;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "events")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String annotation;
    private String description;
    private LocalDateTime createdOn;
    private String location;
    private LocalDateTime eventDate;
    private String title;
    private boolean paid;
    private boolean requestModeration;
    private int participantLimit;
    @ManyToOne
    @JoinColumn(name = "initiator")
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;
    @Enumerated(EnumType.STRING)
    private EventState state;
    private boolean available;
    @ManyToMany(mappedBy = "events")
    private Set<Compilation> compilations;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Event event = (Event) o;
        return getId() != null && Objects.equals(getId(), event.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
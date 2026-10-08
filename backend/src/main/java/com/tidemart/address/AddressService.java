package com.tidemart.address;

import com.tidemart.address.dto.AddressRequest;
import com.tidemart.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {
    private final AddressRepository repo;

    public AddressService(AddressRepository repo) { this.repo = repo; }

    public List<Address> list(Long uid) { return repo.findByUserIdOrderByIdDesc(uid); }

    @Transactional
    public Address save(Long uid, Long id, AddressRequest r) {
        Address a = id == null ? new Address() : find(uid, id);
        boolean first = repo.findByUserIdOrderByIdDesc(uid).isEmpty();
        a.userId = uid; a.name = r.name(); a.phone = r.phone(); a.line1 = r.line1();
        a.city = r.city(); a.state = r.state(); a.pincode = r.pincode();
        a.isDefault = r.isDefault() || (id == null && first);
        if (a.isDefault) {
            for (Address x : repo.findByUserIdOrderByIdDesc(uid)) {
                if (x.isDefault && !x.id.equals(a.id)) { x.isDefault = false; repo.save(x); }
            }
        }
        return repo.save(a);
    }

    public void delete(Long uid, Long id) { repo.delete(find(uid, id)); }

    private Address find(Long uid, Long id) { return repo.findByIdAndUserId(id, uid).orElseThrow(() -> new ResourceNotFoundException("Address not found")); }
}

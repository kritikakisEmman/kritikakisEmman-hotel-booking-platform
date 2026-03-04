package com.thesis.backend.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a hotel image.
 *
 * Storage strategy changed from BLOB to Cloudinary URL:
 *   BEFORE: @Lob byte[] data  -> stored raw image bytes in MySQL (LONGBLOB column)
 *   NOW:    String data       -> stores Cloudinary URL (e.g. https://res.cloudinary.com/...)
 *
 * No Angular changes were needed — templates already use [src]="image.data",
 * which now receives a URL string instead of a base64-encoded byte array.
 *
 * The ManyToOne relationship with Hotel (hotel_id FK) remains unchanged.
 */
@Entity
@Table(name = "images")
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The Cloudinary URL of the image.
     * Example: "https://res.cloudinary.com/manos-k/image/upload/v1234/hotel_abc.jpg"
     * Max length 2048 characters — sufficient for any Cloudinary URL.
     */
    @Column(length = 2048)
    private String data;

    /**
     * The hotel this image belongs to.
     * FetchType.LAZY = not loaded automatically, only when explicitly accessed.
     * @JsonBackReference = prevents infinite JSON serialization loop:
     *   Hotel -> [images] -> Image -> hotel -> Hotel -> ... (would break without this)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Hotel hotel;

    /**
     * Constructor with Cloudinary URL.
     * Called by HotelController after a successful upload to Cloudinary.
     *
     * @param data the secure_url returned by the Cloudinary API
     */
    public Image(String data) {
        this.data = data;
    }

    /** Default no-arg constructor — required by JPA */
    public Image() {
    }
}

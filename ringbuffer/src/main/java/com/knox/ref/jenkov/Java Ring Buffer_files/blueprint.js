window.blueprint = {
	"width": 728,
	"height": 90,
	"images": [
		"txt_2.png",
		"txt_3.png",
		"logo_vimeo.png",
		"cta_1.png",
		"ctaHover_1.png"
	],
	"elements": {
		"stage": {
			"parent": "banner",
			"id": "stage",
			"retina": false,
			"left": 0,
			"top": 0,
			"safeArea": 0,
			"lineHeight": 0,
			"width": 728,
			"height": 90
		},
		"bg": {
			"parent": "banner",
			"id": "bg",
			"retina": false,
			"left": -1,
			"top": 0,
			"safeArea": 0,
			"lineHeight": 0,
			"width": 729,
			"height": 90
		},
		"txt_1": {
			"parent": "banner",
			"id": "txt_1",
			"retina": true,
			"left": 136,
			"top": -4.625,
			"safeArea": 25,
			"lineHeight": 27
		},
		"txt_1_1": {
			"parent": "txt_1",
			"id": "txt_1_1",
			"retina": true,
			"backgroundImage": "data:image/png;base64,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"
		},
		"txt_1_2": {
			"parent": "txt_1",
			"id": "txt_1_2",
			"retina": true,
			"backgroundImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAwYAAADECAYAAAAlDjb2AAAgAElEQVR4Xu3dCdh1XTkH8DtSIiRKRBQiRaVZGTM1aRCiTBkjkSGUMiQhRIgoGTKmVFKEyJAoJYXIGA1UUjQZ4vq7zuk6tr3X2nuf87zP857nt67ru+I7e+3ht9f7fuvea617XaIUAgQIECBAgAABAgTOvcAlzr0AAAIECBAgQIAAAQIESmCgERAgQIAAAQIECBAgIDDQBggQIECAAAECBAgQKIGBRkCAAAECBAgQIECAgMBAGyBAgAABAgQIECBAoAQGGgEBAgQIECBAgAABAgIDbYAAAQIECBAgQIAAgQjISqQdECBAgAABAgQIECAgMNAGCBAgQIAAAQIECBAwYqANECBAgAABAgQIECBgKpE2QIAAAQIECBAgQIBABKwx0A4IECBAgAABAgQIEBAYaAMECBAgQIAAAQIECBgx0AYIECBAgAABAgQIEDCVSBsgQIAAAQIECBAgQCAC1hhoBwQIECBAgAABAgQICAy0AQIECBAgQIAAAQIEjBhoAwQIECBAgAABAgQImEqkDRAgQIAAAQIECBAgEAFrDLQDAgQIECBAgAABAgQEBtoAAQIECBAgQIAAAQJGDLQBAgQIECBAgAABAgRMJdIGCBAgQIAAAQIECBCIgDUG2gEBAgQIECBAgAABAgIDbYAAAQIECBAgQIAAASMG2gABAgQIECBAgAABAqYSaQMECBAgQIAAAQIECETAGgPtgAABAgQIECBAgAABgYE2QIAAAQIECBAgQICAEQNtgAABAgQIECBAgAABU4m0AQIECBAgQIAAAQIEImCNgXZAgAABAgQIECBAgIDAQBsgQIAAAQIECBAgQMCIgTZAgAABAgQIECBAgICpRNoAAQIECBAgQIAAAQIRsMZAOyBAgAABAgQIECBAQGCgDRAgQIAAAQIECBAgYMRAGyBAgAABAgQIECBAwFQibYAAAQIECBAgQIAAgQhYY6AdECBAgAABAgQIECAgMNAGCBAgQIAAAQIECBAwYqANECBAgAABAgQIECBgKpE2QIAAAQIECBAgQIBABKwx0A4IECBAgAABAgQIEBAYaAMECBAgQIAAAQIECBgx0AYIECBAgAABAgQIEDCVSBsgQIAAAQIECBAgQCAC1hhoBwQIECBAgAABAgQICAy0AQIECBAgQIAAAQIEjBhoAwQIECBAgAABAgQImEqkDRAgQIAAAQIECBAgEAFrDLQDAgQIECBAgAABAgQEBtoAAQIECBAgQIAAAQJGDLQBAgQIECBAgAABAgRMJdIGCBAgQIAAAQIECBCIgDUG2gEBAgQIECBAgAABAgIDbYAAAQIECBAgQIAAASMG2gABAgQIECBAgAABAqYSaQMECBAgQIAAAQIECETAGgPtgAABAgQIECBAgAABgYE2QIAAAQIECBAgQICAEQNtgAABAgQIECBAgAABU4m0AQIECBAgQIAAAQIEImCNgXZAgAABAgQIECBAgIDAQBsgQIAAAQIECBAgQMCIgTZAgAABAgQIECBAgICpRNoAAQIECBAgQIAAAQIRsMZAOyBAgAABAgQIECBAQGCgDRAgQIAAAQIECBAgYMRAGyBAgAABAgQIECBAwFQibYAAAQIECBAgQIAAgQhYY6AdECBAgAABAgQIECAgMNAGCBAgQIAAAQIECBAwYqANECBAgAABAgQIECBgKpE2QIAAAQIECBAgQIBABKwx0A4IECBAgAABAgQIEBAYaAMECBAgQIAAAQIECBgx0AYIECBAgAABAgQIEDCVSBsgQIAAAQIECBAgQCAC1hhoBwQIECBAgAABAgQICAy0AQIECBAgQIAAAQIEjBhoAwQIECBAgAABAgQImEqkDRAgQIAAAQIECBAgEAFrDLQDAgQIECBAgAABAgQEBtoAAQIECBAgQIAAAQJGDLQBAgQIECBAgAABAgRMJdIGCBAgQIAAAQIECBCIgDUG2gEBAgQIECBAgAABAgIDbYAAAQIECBAgQIAAASMG2gABAgQIECBAgAABAqYSaQMECBAgQIAAAQIECETAGgPtgAABAgQIECBAgAABgYE2QIAAAQIECBAgQICAEQNtgAABAgQIECBAgAABU4m0AQIECBAgQIAAAQIEImCNgXZAgAABAgQIECBAgIDAQBsgQIAAAQIECBAgQMCIgTZAgAABAgQIECBAgICpRNoAAQIECBAgQIAAAQIRsMZAOyBAgAABAgQIECBAQGCgDRAgQIAAAQIECBAgYMRAGyBAgAABAgQIECBAwFQibYAAAQIECBAgQIAAgQhYY6AdECBAgAABAgQIECAgMNAGCBAgQIAAAQIECBAwYqANECBAgAABAgQIECBgKpE2QIAAAQIE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AgQIECBAgMDxCQgMju+deiICBAgQIECAAAECiwUEBovJVCBAgAABAgQIECBwfAICg+N7p56IAAECBAgQIECAwGIBgcFiMhUIECBAgAABAgQIHJ+AwOD43qknIkCAAAECBAgQILBYQGCwmEwFAgQIECBAgAABAscnIDA4vnfqiQgQIECAABp1G30AAAEfSURBVAECBAgsFhAYLCZTgQABAgQIECBAgMDxCQgMju+deiICBAgQIECAAAECiwUEBovJVCBAgAABAgQIECBwfAICg+N7p56IAAECBAgQIECAwGIBgcFiMhUIECBAgAABAgQIHJ+AwOD43qknIkCAAAECBAgQILBYQGCwmEwFAgQIECBAgAABAscnIDA4vnfqiQgQIECAAAECBAgsFhAYLCZTgQABAgQIECBAgMDxCQgMju+deiICBAgQIECAAAECiwUEBovJVCBAgAABAgQIECBwfAICg+N7p56IAAECBAgQIECAwGIBgcFiMhUIECBAgAABAgQIHJ+AwOD43qknIkCAAAECBAgQILBYQGCwmEwFAgQIECBAgAABAscn8D//Rx4Q1m5wDQAAAABJRU5ErkJggg=="
		},
		"txt_2": {
			"parent": "banner",
			"id": "txt_2",
			"retina": true,
			"left": 136,
			"top": -3.25,
			"safeArea": 25,
			"lineHeight": 26.75,
			"backgroundImage": "txt_2.png",
			"width": 278,
			"height": 71
		},
		"txt_3": {
			"parent": "banner",
			"id": "txt_3",
			"retina": true,
			"left": 146,
			"top": 37,
			"safeArea": 15,
			"lineHeight": 15.600000000000001,
			"backgroundImage": "txt_3.png",
			"width": 147,
			"height": 43
		},
		"logo_vimeo": {
			"parent": "banner",
			"id": "logo_vimeo",
			"retina": true,
			"left": 22,
			"top": 34,
			"safeArea": 0,
			"lineHeight": 0,
			"backgroundImage": "logo_vimeo.png",
			"width": 82,
			"height": 23
		},
		"cta": {
			"parent": "banner",
			"id": "cta",
			"retina": true,
			"left": 582,
			"top": 27,
			"safeArea": 0,
			"lineHeight": 15.600000000000001
		},
		"cta_1": {
			"parent": "cta",
			"id": "cta_1",
			"retina": true,
			"backgroundImage": "cta_1.png"
		},
		"ctaHover": {
			"parent": "banner",
			"id": "ctaHover",
			"retina": true,
			"left": 582,
			"top": 27,
			"safeArea": 0,
			"lineHeight": 15.600000000000001
		},
		"ctaHover_1": {
			"parent": "ctaHover",
			"id": "ctaHover_1",
			"retina": true,
			"backgroundImage": "ctaHover_1.png"
		},
		"top": {
			"parent": "banner",
			"id": "top",
			"retina": false,
			"left": 0,
			"top": -10,
			"safeArea": 0,
			"lineHeight": 0,
			"width": 728,
			"height": 10
		},
		"bottom": {
			"parent": "banner",
			"id": "bottom",
			"retina": false,
			"left": 0,
			"top": 90,
			"safeArea": 0,
			"lineHeight": 0,
			"width": 728,
			"height": 10
		},
		"left": {
			"parent": "banner",
			"id": "left",
			"retina": false,
			"left": -10,
			"top": 0,
			"safeArea": 0,
			"lineHeight": 0,
			"width": 10,
			"height": 90
		},
		"right": {
			"parent": "banner",
			"id": "right",
			"retina": false,
			"left": 728,
			"top": 0,
			"safeArea": 0,
			"lineHeight": 0,
			"width": 10,
			"height": 90
		}
	},
	"settings": {
		"filename": "Q3-2019_EN_mid-funnel_marketer_rapid-transfer_1_learn-more_728x90_HTML5",
		"market": "EN",
		"campaign": "Q3-2019",
		"funnel": "mid-funnel",
		"segment": "marketer",
		"feature": "rapid-transfer",
		"copyVariation": "1",
		"size": "728x90",
		"marketIndex": "1",
		"id": "marketerrapid-transfer1",
		"copy": {
			"copy1": "Ship it before someone says \"what about...?\"",
			"copy2": "Rapid File Transfer.",
			"copy3": "A Vimeo Feature",
			"copy4": "Learn More",
			"copy5": ""
		},
		"fileType": "jpg",
		"fallback": true,
		"HtmlfileType": "html",
		"backgroundColor": "#94cd91"
	}
};
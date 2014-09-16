package com.ariatemplates.tools.ide.backend.exception



class BackendException extends Exception {
	private static final long serialVersionUID = 1L



	static buildMessage(content) {
		def msg = content["msg"]
		content.remove "msg"
		"$msg: $content"
	}

	BackendException(Map<String, Object> content) {
		super(this.class.buildMessage(content))
	}
}

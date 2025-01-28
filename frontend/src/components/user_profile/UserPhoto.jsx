import React, { Component } from 'react';
import ContendCard from '../contents/ContentCard';
import $ from 'jquery';
import imageCompression from 'browser-image-compression';
import { connect } from 'react-redux';
import GET_INFO_TIMEOUT from '../config/GetInfoTimeoutConfig';
import BACKEND_ADDRESS_URL from '../config/BackendAddressURLConfig';

class UserPhoto extends Component {
    state = {
        photo_base64: '',
        photo_preview_base64: '',
        photo_size_str: '--',
        encoded_size: '--',

        error_message: "",
        photo_change: false,
        is_loading: false,
    }

    componentDidMount = () => {
        if (this.props.is_login) {
            this.handleGetPhoto();
        } else {
            setTimeout(this.handleGetPhoto, GET_INFO_TIMEOUT);
        }
    }

    handleGetPhoto = () => {
        const token = this.props.token;
        // console.log(token);
        this.setState({
            is_loading: true,
            // error_message: '',
        });
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/user/account/photo/",
            type: "GET",
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                this.setState({
                    photo_base64: resp.photo,
                    photo_preview_base64: resp.photo,
                    is_loading: false
                });
            }
        });
    }

    handleSubmit = () => {
        // console.log("submit");
        this.setState({
            error_message: '',
            is_loading: true
        });
        if (this.state.photo_preview_base64.length > 50000) {
            this.setState({
                error_message: "Image size exceeds 50KB limit",
                is_loading: false
            });
        } else if (this.state.photo_preview_base64 === '') {
            this.setState({
                error_message: "Image cannot be empty",
                is_loading: false
            });
        } else {
            const token = this.props.token;
            $.ajax({
                url: BACKEND_ADDRESS_URL + "/user/account/update_photo",
                type: "PUT",
                data: {
                    photo: this.state.photo_preview_base64,
                },
                headers: {
                    Authorization: "Bearer " + token
                },
                success: (resp) => {
                    // console.log(resp);
                    if (resp.error_message === 'success') {
                        this.setState({
                            error_message: "Image modified successfully",
                            is_loading: false,
                            photo_change: false,
                        }, () => {
                            this.handleGetPhoto();
                        });
                    } else {
                        this.setState({
                            error_message: resp.error_message,
                            is_loading: false,
                        });
                    }
                }
            });
        }
    }

    returnFileSize(number) {
        if (number < 1024) {
            return `${number} bytes`;
        } else if (number >= 1024 && number < 1048576) {
            return `${(number / 1024).toFixed(1)} KB`;
        } else if (number >= 1048576) {
            return `${(number / 1048576).toFixed(1)} MB`;
        }
    }

    handleFileInput = (e) => {
        const file = e.target.files[0];
        // console.log("file size: ", file.size);

        const reader = new FileReader();

        reader.onload = (e) => {
            // console.log("encoded length: ", e.target.result.length);
            // console.log(e.target.result);
            this.setState({
                // photo_base64: e.target.result,
                photo_preview_base64: e.target.result,
                photo_size_str: this.returnFileSize(file.size),
                encoded_size: this.returnFileSize(e.target.result.length),
                photo_change: true,
            }, () => {
                // const $imagePreview = $('#imagePreview');
                // console.log($imagePreview);
                // $imagePreview.show();
            });
        }

        if (file) {
            // compress
            const options = {
                // maxSizeMB: 1,
                maxWidthOrHeight: 200,
                useWebWorker: true
            };
            imageCompression(file, options).then((compressedFile) => {
                reader.readAsDataURL(compressedFile);
            });
        }
    }

    renderSpinner = () => {
        if (this.state.is_loading) {
            return (
                <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
            );
        }
    }

    renderImage = () => {
        const style = { height: "200px", width: "200px" };
        if (this.state.photo_base64 === '') {
            return (
                <div className="text-center img-thumbnail" style={{ ...style, lineHeight: "200px" }}>
                    <span className="align-top">
                        No avatar set
                    </span>
                </div>
            );
        } else if (this.state.photo_base64) {
            return (
                <img className='img-thumbnail' src={this.state.photo_base64} alt="用户头像" style={style} />
            );
        }
    }


    renderImagePreview = () => {
        const style = { height: "200px", width: "200px" };
        if (this.state.photo_preview_base64 !== '') {
            return (
                <div className="d-flex justify-content-center">
                    <img id='imagePreview' className='img-thumbnail' src={this.state.photo_preview_base64} alt="上传预览" style={{ height: "200px", width: "200px" }} />
                </div>
            );
        }
    }

    addImageRender = () => {
        return (
            <React.Fragment>
                <button type="button" className="btn btn-secondary" data-bs-toggle="modal" data-bs-target="#addImageModal">
                    Update Avatar
                </button>

                <div className="modal fade" id="addImageModal" tabIndex="-1" aria-labelledby="addImageModalLabel" aria-hidden="true">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h1 className="modal-title fs-5" id="addImageModalLabel">Upload Avatar</h1>
                                <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <div className="row justify-content-between">
                                    <div className="col-auto">
                                        <p>Image size limit: 50KB</p>
                                    </div>
                                    <div className="col-auto">
                                        <p>Current image size: {this.state.encoded_size}</p>
                                    </div>
                                </div>
                                {this.renderImagePreview()}
                                <input type="file" accept='image/jpeg, image/png, image/jpg' className="form-control mt-2" id="inputImage" onChange={(e) => this.handleFileInput(e)} />
                            </div>
                            <div className="modal-footer">
                                <span className="align-middle" style={{ color: "red" }}>{this.state.error_message}</span>
                                <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                <button type="button" className="btn btn-primary" disabled={!this.state.photo_change || this.state.is_loading} onClick={() => this.handleSubmit()}>
                                    {this.renderSpinner()}
                                    Submit
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </React.Fragment>
        );
    }

    render() {
        return (
            <div className="container-fluid">
                <ContendCard>
                    <h4>User Avatar</h4>
                    <hr />
                    <div className="d-flex justify-content-center">
                        {this.renderImage()}
                    </div>
                    <div className="row justify-content-center mt-2">
                        <div className="col-auto">
                            {this.addImageRender()}
                        </div>
                    </div>
                </ContendCard>
            </div>
        );
    }
}

const mapStateToProps = (state, props) => {
    return {
        ...props,
        user_id: state.user_id,
        username: state.username,
        name: state.name,
        permission: state.permission,
        token: state.token,
        is_login: state.is_login,
    };
};

export default connect(mapStateToProps, null)((props) =>
    <UserPhoto
        {...props}
    />
);
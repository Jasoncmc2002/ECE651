import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { connect } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import ContendCard from './contents/ContentCard';
import $ from 'jquery';
import ACTIONS from '../redux/actions';
import BACKEND_ADDRESS_URL from "./config/BackendAddressURLConfig";
import UserPhoto from './user_profile/UserPhoto';
import GET_INFO_TIMEOUT from './config/GetInfoTimeoutConfig';

class UserProfile extends Component {
    state = {
        username: "",
        name: "",
        permission: null,
        user_info_change: false,

        old_password: "",
        password: "",
        password_confirm: "",
        password_change: false,

        error_message: "",
        password_change_message: "",

        is_loading: false,
    }

    componentDidMount = () => {  // this code will cause a controlled-uncontrolled warning! U should use this " value={this.state.fields.name || ''} " instead.
        this.setState({
            username: this.props.username ? this.props.username : "",
            name: this.props.name ? this.props.name : "",
            permission: this.props.permission ? this.props.permission : null,
        });
        if (this.props.is_login) {
            this.setState({
                username: this.props.username,
                name: this.props.name,
                permission: this.props.permission
            });
        } else {
            setTimeout(() => {
                this.setState({
                    username: this.props.username,
                    name: this.props.name,
                    permission: this.props.permission
                });
            }, GET_INFO_TIMEOUT);
        }
    }

    handleLoadingRender = () => {
        if (this.state.is_loading) {
            return (
                <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
            );
        }
    }


    handleUpdate(e) {
        // console.log(this.state);
        e.preventDefault();
        this.setState({
            error_message: "",
            is_loading: true,
        });
        const prop_username = this.props.username;
        const prop_name = this.props.name;
        if (prop_username && this.state.username === prop_username && prop_name && this.state.name === prop_name) {
            this.setState({
                error_message: "Information has not been modified",
                is_loading: false
            });
            // console.log("information unchanged");
            return;
        }
        if (this.state.username === "") {
            this.setState({
                error_message: "Username cannot be empty",
                is_loading: false
            });
        } else if (this.state.name === "") {
            this.setState({
                error_message: "Name cannot be empty",
                is_loading: false
            });
        } else {
            // console.log("ready to update");
            // console.log(this.state.username, this.state.name);
            // console.log(this.props.token);
            const token = this.props.token;
            $.ajax({
                url: BACKEND_ADDRESS_URL + "/user/account/update_user_info/",
                type: "POST",
                data: {
                    username: this.state.username,
                    name: this.state.name,
                },
                headers: {
                    Authorization: "Bearer " + token
                },
                success: (resp) => {
                    // console.log(resp);
                    if (resp.error_message === "success") {
                        $.ajax({
                            url: BACKEND_ADDRESS_URL + "/user/account/info/",
                            type: "GET",
                            headers: {
                                Authorization: "Bearer " + token
                            },
                            success: (resp) => {
                                if (resp.error_message === "success") {
                                    const name = resp.name;
                                    const permission = parseInt(resp.permission);
                                    const user_id = parseInt(resp.user_id);
                                    const username = resp.username;
                                    this.props.update_user({
                                        user_id: user_id,
                                        username: username,
                                        name: name,
                                        permission: permission
                                    });
                                    this.setState({
                                        error_message: "Information updated successfully",
                                        user_info_change: false,
                                        is_loading: false
                                    });
                                } else {
                                    console.log(resp);
                                }
                            }
                        });
                    } else {
                        this.setState({
                            error_message: resp.error_message,
                            is_loading: false
                        });
                    }
                },
                error: (resp) => {
                    this.setState({ error_message: "Update failed (Please see the console)" });
                }
            });
        }
    }

    handlePasswordChange = (e) => {
        e.preventDefault();
        this.setState({
            password_change_message: "",
            is_loading: true,
        });
        if (this.state.old_password === "") {
            this.setState({
                password_change_message: "The original password cannot be empty",
                is_loading: false
            })
        } else if (this.state.password === "") {
            this.setState({
                password_change_message: "Password cannot be empty",
                is_loading: false
            })
        } else if (this.state.old_password === this.state.password) {
            this.setState({
                password_change_message: "The new password cannot be the same as the original password",
                is_loading: false
            });
        } else if (this.state.password_confirm === "") {
            this.setState({
                password_change_message: "Confirm password cannot be empty",
                is_loading: false
            })
        } else if (this.state.password !== this.state.password_confirm) {
            this.setState({
                password_change_message: "The passwords you entered twice do not match",
                is_loading: false
            })
        } else {
            // console.log(this.state.old_password, this.state.password, this.state.password_confirm);
            // console.log(this.props.token);
            const token = this.props.token;
            $.ajax({
                url: BACKEND_ADDRESS_URL + "/user/account/update_password/",
                type: "POST",
                data: {
                    originalPassword: this.state.old_password,
                    password: this.state.password,
                    confirmedPassword: this.state.password_confirm
                },
                headers: {
                    Authorization: "Bearer " + token
                },
                success: (resp) => {
                    console.log(resp);
                    if (resp.error_message === "success") {
                        this.setState({
                            old_password: "",
                            password: "",
                            password_confirm: "",
                            password_change: false,
                            password_change_message: "Password updated successfully",
                            is_loading: false,
                        });
                    } else {
                        this.setState({
                            password_change_message: resp.error_message,
                            is_loading: false
                        });
                    }
                },
                error: (resp) => {
                    this.setState({ password_change_message: "Password update failed (Please see the console)" });
                }
            });
        }
    }

    render_permission() {
        if (this.props.permission >= 2) {
            return (
                <div className="mb-3">
                    <div>Permission</div>
                    <div className="d-flex justify-content-around">
                        <div className="form-check">
                            <input className="form-check-input" type="radio" name="permission" id="regularPermission" value={"1"} disabled />
                            <label className="form-check-label" htmlFor="regularPermission">
                                Student
                            </label>
                        </div>
                        <div className="form-check">
                            <input className="form-check-input" type="radio" name="permission" id="teacherPermission" value={"2"} disabled />
                            <label className="form-check-label" htmlFor="teacherPermission">
                                Teacher
                            </label>
                        </div>
                        <div className="form-check">
                            <input className="form-check-input" type="radio" name="permission" id="adminPermission" value={"3"} checked disabled />
                            <label className="form-check-label" htmlFor="adminPermission">
                                Administrator
                            </label>
                        </div>
                    </div>
                </div>
            );
        } else if (this.props.permission >= 1) {
            return (
                <div className="mb-3">
                    <div>Permission</div>
                    <div className="d-flex justify-content-around">
                        <div className="form-check">
                            <input className="form-check-input" type="radio" name="permission" id="regularPermission" value={"1"} disabled />
                            <label className="form-check-label" htmlFor="regularPermission">
                                Student
                            </label>
                        </div>
                        <div className="form-check">
                            <input className="form-check-input" type="radio" name="permission" id="teacherPermission" value={"2"} checked disabled />
                            <label className="form-check-label" htmlFor="teacherPermission">
                                Teacher
                            </label>
                        </div>
                        <div className="form-check">
                            <input className="form-check-input" type="radio" name="permission" id="adminPermission" value={"3"} disabled />
                            <label className="form-check-label" htmlFor="adminPermission">
                                Administrator
                            </label>
                        </div>
                    </div>
                </div>
            );
        } else {
            return (
                <div className="mb-3">
                    <div>Permission</div>
                    <div className="d-flex justify-content-around">
                        <div className="form-check">
                            <input className="form-check-input" type="radio" name="permission" id="regularPermission" value={"1"} checked disabled />
                            <label className="form-check-label" htmlFor="regularPermission">
                                Student
                            </label>
                        </div>
                        <div className="form-check">
                            <input className="form-check-input" type="radio" name="permission" id="teacherPermission" value={"2"} disabled />
                            <label className="form-check-label" htmlFor="teacherPermission">
                                Teacher
                            </label>
                        </div>
                        <div className="form-check">
                            <input className="form-check-input" type="radio" name="permission" id="adminPermission" value={"3"} disabled />
                            <label className="form-check-label" htmlFor="adminPermission">
                                Administrator
                            </label>
                        </div>
                    </div>
                </div>
            );
        }
    }

    handleAccountRender = () => {
        if (this.props.is_login) {
            return (
                <div className="container">
                    <div className="row" style={{ height: "90vh" }}>
                        <div className="col-lg-4">
                            <UserPhoto />
                        </div>
                        <div className="col-lg-8">
                            <ContendCard>
                                <h4>User Information</h4>
                                <hr />
                                <form>
                                    <div className="mb-3">
                                        <label htmlFor="username" className="form-label">Username</label>
                                        <input type="text" className="form-control" id="username" name='username' defaultValue={this.props.username || ''} onChange={(e) => { this.setState({ username: e.target.value, user_info_change: true }); }} />
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="name" className="form-label">Name</label>
                                        <input type="url" className="form-control" id="name" name='name' defaultValue={this.props.name || ''} onChange={(e) => { this.setState({ name: e.target.value, user_info_change: true }); }} />
                                    </div>
                                    {this.render_permission()}
                                    <div className="error-message mb-3" style={{ color: "red" }}>{this.state.error_message}</div>
                                    <div className="mb-3" style={{ display: 'flex', justifyContent: 'center' }}>
                                        <button type="submit" className="btn btn-success" onClick={(e) => { this.handleUpdate(e); }} disabled={!this.state.user_info_change || this.state.is_loading}>
                                            {this.handleLoadingRender()}
                                            Update Information
                                        </button>
                                    </div>
                                </form>
                            </ContendCard>

                            <ContendCard>
                                <h4>Change Password</h4>
                                <hr />
                                <form>
                                    <div className="mb-3">
                                        <label htmlFor="old_password" className="form-label">Original Password</label>
                                        <input type="password" className="form-control" id="old_password" name="old_password" value={this.state.old_password} onChange={(e) => { this.setState({ old_password: e.target.value, password_change: true }) }} />
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="password" className="form-label">Password</label>
                                        <input type="password" className="form-control" id="password" name="password" value={this.state.password} onChange={(e) => { this.setState({ password: e.target.value, password_change: true }) }} />
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="password_confirm" className="form-label">Confirm Password</label>
                                        <input type="password" className="form-control" id="password_confirm" name="password_confirm" value={this.state.password_confirm} onChange={(e) => { this.setState({ password_confirm: e.target.value, password_change: true }) }} />
                                    </div>
                                    <div className="password-change-error-message mb-3" style={{ color: "red" }}>
                                        {this.state.password_change_message}
                                    </div>
                                    <div className="d-flex justify-content-center">
                                        <button type="submit" className="btn btn-primary" onClick={(e) => this.handlePasswordChange(e)} disabled={!this.state.password_change || this.state.is_loading}>
                                            {this.handleLoadingRender()}
                                            Change Password
                                        </button>
                                    </div>
                                </form>
                            </ContendCard>
                        </div>
                    </div>
                </div>
            );
        } else {
            return (
                <div className="container">
                    <div className="row justify-content-md-center align-items-center" style={{ height: "90vh" }}>
                        <div className="col col-md-7">
                            <ContendCard>
                                <div className="row justify-content-md-center">
                                    <h1 className="text-center">Update User Information</h1>
                                    <hr />
                                    <h4 className="text-center">Please <Link className='btn btn-link px-0' to="/login/" style={{ textDecoration: "none" }}><h4 className='mb-1'>sign in</h4></Link> to access</h4>
                                </div>
                            </ContendCard>
                        </div>
                    </div>
                </div>
            );
        }
    }

    render() {
        return (
            <React.Fragment>
                {this.handleAccountRender()}
            </React.Fragment>
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

const mapDispatchToProps = {
    update_user: (data) => {
        return {
            type: ACTIONS.UPDATE_USER,
            user_id: data.user_id,
            username: data.username,
            name: data.name,
            permission: data.permission
        };
    },
};

export default connect(mapStateToProps, mapDispatchToProps)((props) =>
    <UserProfile
        {...props}
        navigate={useNavigate()}
    />
);
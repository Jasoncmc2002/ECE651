import React, { Component } from 'react';
import ContendCard from './contents/ContentCard';
import ACTIONS from '../redux/actions';
import { connect } from 'react-redux';
import $ from 'jquery';
import { useNavigate } from 'react-router-dom';
import { Link } from 'react-router-dom';
import BACKEND_ADDRESS_URL from "./config/BackendAddressURLConfig";
import { ArrowRight } from 'react-bootstrap-icons';

class Login extends Component {
    state = {
        username: "",
        password: "",
        error_message: "",
        remember_me: true,

        is_loading: false,
    }

    handleLogin = (e) => {
        e.preventDefault(); // 阻止表单的默认提交行为
        this.setState({
            error_message: "",
            is_loading: true
        });
        // console.log("click");
        if (this.state.username === "") {
            this.setState({
                error_message: "Username cannot be empty",
                is_loading: false
            });
        } else if (this.state.password === "") {
            this.setState({
                error_message: "Password cannot be empty",
                is_loading: false
            });
        } else {
            // console.log("login");
            $.ajax({
                url: BACKEND_ADDRESS_URL + "/user/account/token/",
                type: "POST",
                data: {
                    username: this.state.username,
                    password: this.state.password,
                },
                success: (resp) => {
                    // console.log(resp);
                    if (resp.error_message === "success") {
                        const token = resp.token;
                        this.props.update_token({ token: token });
                        // console.log(this.props.token); 注意这一步token已经存进去了，但是下面就是不让用

                        if (this.state.remember_me) {
                            localStorage.setItem("token", token);
                        }

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
                                    this.props.navigate(-1); // 退回上一个页面
                                } else {
                                    console.log(resp);
                                }
                            },
                            error: (resp) => {
                                console.log(resp);
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
                    // console.log(resp)
                    this.setState({
                        error_message: "Incorrect username or password",
                        is_loading: false
                    })
                }
            });
        }
    }

    handleLoadingRender = () => {
        if (this.state.is_loading) {
            return (
                <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
            );
        }
    }

    render() {
        const style = { "--bs-icon-link-transform": "translate3d(.25em, 0, 0)" };
        return (
            <div className="container">
                <div className="row justify-content-md-center align-items-center" style={{ height: "90vh" }}>
                    <div className="col col-md-5">
                        <ContendCard>
                            <h4 className='text-center'>Sign In</h4>
                            <hr className='m-1' />
                            <form action='https://app3117.acapp.acwing.com.cn/settings/calculator/login/'>
                                <div className="mb-3">
                                    <label htmlFor="username" className="form-label">Username</label>
                                    <input type="text" className="form-control" id="username" name="username" onChange={(e) => { this.setState({ username: e.target.value }) }} />
                                </div>
                                <div className="mb-3">
                                    <label htmlFor="password" className="form-label">Password</label>
                                    <input type="password" className="form-control" id="password" name="password" onChange={(e) => { this.setState({ password: e.target.value }) }} />
                                </div>
                                <div className="mb-3 form-check">
                                    <input type="checkbox" className="form-check-input" id="remember_me" onChange={(e) => { this.setState({ remember_me: !this.state.remember_me }) }} checked={this.state.remember_me} />
                                    <label className="form-check-label" htmlFor="remember_me">Keep me logged in</label>
                                    <Link className='icon-link icon-link-hover float-end' to="/register/" style={style}>
                                        Register
                                        <ArrowRight />
                                    </Link>
                                </div>
                                <div className="error-message mb-3" style={{ height: "2rem", color: "red" }}>
                                    {this.state.error_message}
                                </div>
                                <button type="submit" className="btn btn-primary" style={{ width: "100%" }} onClick={(e) => this.handleLogin(e)} disabled={this.state.is_loading}>
                                    {this.handleLoadingRender()}
                                    Sign In
                                </button>
                            </form>
                        </ContendCard>
                    </div>
                </div>
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
    update_token: (data) => {
        return {
            type: ACTIONS.UPDATE_TOKEN,
            token: data.token,
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)((props) =>
    <Login
        {...props}
        navigate={useNavigate()}
    />
);